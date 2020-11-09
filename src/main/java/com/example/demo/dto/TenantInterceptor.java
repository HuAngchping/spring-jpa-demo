package com.example.demo.dto;

import com.example.demo.util.SaasUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huang
 * @create 2020/11/2
 */
public class TenantInterceptor implements StatementInspector {

    /**
     * 保存需要被拦截sql的表名
     */
    public static List<String> tenantTables = new ArrayList<>();

    /**
     * 数据库表租户字段名
     */
    private final String tenantIdColumn = "tenant_id";

    /**
     * 拦截查询sql，并修改sql增加租户信息
     * 只处理查询语句
     *
     * @param sql
     * @return
     */
    @Override
    public String inspect(String sql) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            if (stmt instanceof Select) {
                sql = filterSelect(stmt);
            }
            System.out.println(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return sql;
    }


    private String filterSelect(Statement statement) {
        filterSelectBody(((Select) statement).getSelectBody());
        return statement.toString();
    }

    private void filterSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            filterPlainSelect((PlainSelect) selectBody);
        }
    }

    /**
     * 当sql语句from多表，from后面第一个表不是要拦截的表时，被当成join查询处理
     *
     * @param plainSelect
     */
    private void filterPlainSelect(PlainSelect plainSelect) {
        List<Join> joinList = plainSelect.getJoins();
        if (joinList != null && !joinList.isEmpty()) {
            joinList.forEach(join -> {
                filterJoin(join, plainSelect);
            });
        }

        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            String tableName = fromTable.getFullyQualifiedName();
            if (tenantTables.contains(tableName)) {
                Expression whereExpression = plainSelect.getWhere();
                plainSelect.setWhere(setWhereExpression(whereExpression, fromTable));
            } else {
                Expression whereExpression = plainSelect.getWhere();
                // 过滤条件查询中是否包含in
                filterInAndSubQuery(whereExpression);
            }
        } else if (fromItem instanceof SubSelect) {
            // 当查询语句为：select * from (select * from country)时
            SubSelect subSelect = (SubSelect) fromItem;
            filterSelectBody(subSelect.getSelectBody());
        }
    }

    /**
     * 过滤单表查询时，条件中包含in
     *
     * @param expression
     */
    private void filterInAndSubQuery(Expression expression) {
        if (expression instanceof OrExpression) {
            // 处理 or 语句
            OrExpression orExpression = (OrExpression) expression;
            Expression right = orExpression.getRightExpression();

            filterInSubQuery(right);

            Expression leftExpression = orExpression.getLeftExpression();
            if (leftExpression != null) {
                // 当左侧表达式不为空时，继续过滤
                filterInAndSubQuery(leftExpression);
            }
        } else if (expression instanceof AndExpression) {
            // 处理 and 语句
            AndExpression andExpression = (AndExpression) expression;
            Expression right = andExpression.getRightExpression();

            filterInSubQuery(right);

            Expression leftExpression = andExpression.getLeftExpression();
            if (leftExpression != null) {
                filterInAndSubQuery(leftExpression);
            }
        } else if (expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            ItemsList itemsList = inExpression.getRightItemsList();
            if (itemsList instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) itemsList;
                filterSelectBody(subSelect.getSelectBody());
            }
        } else if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            Expression rightExpression = equalsTo.getRightExpression();
            if (rightExpression instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) rightExpression;
                filterSelectBody(subSelect.getSelectBody());
            }
        }
    }

    /**
     * 过滤 join on
     *
     * @param join
     * @param plainSelect
     */
    private void filterJoin(Join join, PlainSelect plainSelect) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            String tableName = fromTable.getFullyQualifiedName();
            if (tenantTables.contains(tableName)) {
                Expression onExpression = join.getOnExpression();
                if (onExpression != null) {
                    join.setOnExpression(setWhereExpression(onExpression, fromTable));
                } else {
                    plainSelect.setWhere(setWhereExpression(plainSelect.getWhere(), fromTable));
                }
            }
        }
    }

    /**
     * 过滤 in 子查询 语句
     *
     * @param rightExpression
     */
    private void filterInSubQuery(Expression rightExpression) {
        if (rightExpression instanceof InExpression) {
            // 处理 id in (select id from country) 类语句
            InExpression inExpression = (InExpression) rightExpression;
            ItemsList itemsList = inExpression.getRightItemsList();
            if (itemsList instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) itemsList;
                filterSelectBody(subSelect.getSelectBody());
            }
        } else if (rightExpression instanceof EqualsTo) {
            // 处理 id = (select id from country) 类自查询语句
            EqualsTo equalsTo = (EqualsTo) rightExpression;
            Expression equalsRight = equalsTo.getRightExpression();
            if (equalsRight instanceof SubSelect) {
                SubSelect subSelect = (SubSelect) equalsRight;
                filterSelectBody(subSelect.getSelectBody());
            }
        }
    }

    /**
     * 当sql语句为单表查询时:
     * select * from country c where c.id = 1 or c.name = 'a' and c.p = 'p' and c.a = 2 order by c.id desc
     * 当sql语句from多表，from后面第一个表为要拦截的表时，逻辑与单表一样
     * select country.* from country, customer where country.id = customer.country_id and country.age = 20 limit 10
     *
     * @param whereExpression
     * @param fromTable
     * @return
     */
    private Expression setWhereExpression(Expression whereExpression, Table fromTable) {
        int tenantId = SaasUtils.getTenantId();
        final Expression tenantExpression = new LongValue(tenantId);
        Expression appendExpression = new EqualsTo();
        ((EqualsTo) appendExpression).setLeftExpression(getAliasColumn(fromTable));
        ((EqualsTo) appendExpression).setRightExpression(tenantExpression);
        if (whereExpression == null) {
            return appendExpression;
        }
        // 如果where条件中包含or，(whereExpression instanceof OrExpression) == true
        if (whereExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(whereExpression), appendExpression);
        } else {
            return new AndExpression(whereExpression, appendExpression);
        }
    }

    private Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (null == table.getAlias()) {
            column.append(table.getName());
        } else {
            column.append(table.getAlias().getName());
        }
        column.append(".");
        column.append(tenantIdColumn);
        return new Column(column.toString());
    }

}
