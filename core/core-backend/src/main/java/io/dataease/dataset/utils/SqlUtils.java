package io.dataease.dataset.utils;

import com.google.common.collect.ImmutableList;
import io.dataease.exception.DEException;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.calcite.sql.SqlKind.*;

/**
 * SQL处理工具类
 * 基于Apache Calcite提供SQL解析、转换和优化功能
 * 主要用于为SQL语句添加schema信息和进行语法分析
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>SQL语句解析和AST语法树构建</li>
 *   <li>为表名添加schema前缀</li>
 *   <li>SQL语句的格式化和优化</li>
 *   <li>支持各种SQL语句类型的处理</li>
 * </ul>
 *
 * @author Junjun
 */
public class SqlUtils {
    public static Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    /**
     * 为SQL语句中的表名添加schema信息
     * 解析SQL语句并为其中的表名添加指定的schema前缀
     *
     * @param sql    原始SQL语句
     * @param schema 要添加的schema名称
     * @return 添加schema后的SQL语句
     * @throws DEException 当SQL解析失败时抛出异常
     */
    public static String addSchema(String sql, String schema) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        SqlParser.Config config =
                SqlParser.config()
                        .withLex(Lex.JAVA)
                        .withIdentifierMaxLength(256);
        // 创建解析器
        SqlParser sqlParser = SqlParser
                .create(sql, config);
        // 生成 AST 语法树
        SqlNode sqlNode = null;
        try {
            sqlNode = sqlParser.parseStmt();
            addTableSchema(sqlNode, false, schema, config);
        } catch (SqlParseException e) {
            DEException.throwException("使用 Calcite 进行语法分析发生了异常:" + e);
        }
        String sqlRender = sqlNode.toString();
        // 处理sql中多余的`都替换成1个
        sqlRender = sqlRender.replaceAll("(`+)", "`");
        return sqlRender;
    }

    /**
     * 递归为SQL语法树中的表名添加schema信息
     * 遍历SQL语法树的各个节点，识别表名并添加schema前缀
     * 支持多种SQL语句类型：SELECT、UNION、JOIN、WHERE条件等
     *
     * @param sqlNode    SQL语法树节点
     * @param fromOrJoin 是否处于FROM或JOIN子句中
     * @param schema     要添加的schema名称
     * @param config     SQL解析器配置
     * @throws DEException 当语法分析异常时抛出
     */
    private static void addTableSchema(SqlNode sqlNode, Boolean fromOrJoin, String schema, SqlParser.Config config) {
        try {
            if (sqlNode.getKind() == JOIN) {
                SqlJoin sqlKind = (SqlJoin) sqlNode;
                addTableSchema(sqlKind.getLeft(), true, schema, config);
                addTableSchema(sqlKind.getRight(), true, schema, config);
            } else if (sqlNode.getKind() == IDENTIFIER) {
                if (fromOrJoin) {
                    // 获取表名
                    String tableName = sqlNode.toString();
                    SqlIdentifier sqlKind = (SqlIdentifier) sqlNode;
                    sqlKind.setNames(ImmutableList.of(schema + "`.`" + tableName), null);
                }
            } else if (sqlNode.getKind() == AS) {
                SqlBasicCall sqlKind = (SqlBasicCall) sqlNode;
                if (sqlKind.getOperandList().size() >= 2) {
                    addTableSchema(sqlKind.getOperandList().get(0), fromOrJoin, schema, config);
                }
            } else if (sqlNode.getKind() == SELECT) {
                SqlSelect sqlKind = (SqlSelect) sqlNode;

                // 解析from
                addTableSchema(sqlKind.getFrom(), true, schema, config);

                // 解析where
                SqlBasicCall where = (SqlBasicCall) sqlKind.getWhere();
                if (where != null && where.getOperandList().size() >= 2) {
                    for (int i = 0; i < where.getOperandList().size(); i++) {
                        addTableSchema(where.getOperandList().get(i), false, schema, config);
                    }
                }
            } else if (sqlNode.getKind() == UNION) {
                SqlBasicCall sqlKind = (SqlBasicCall) sqlNode;
                // 使用union，至少会有2个子SQL，否则语法不正确
                if (sqlKind.getOperandList().size() >= 2) {
                    for (int i = 0; i < sqlKind.getOperandList().size(); i++) {
                        addTableSchema(sqlKind.getOperandList().get(i), fromOrJoin, schema, config);
                    }
                }
            } else if (sqlNode.getKind() == ORDER_BY) {
                SqlOrderBy sqlKind = (SqlOrderBy) sqlNode;
                List<SqlNode> operandList = sqlKind.getOperandList();
                if (operandList.size() > 0) {
                    addTableSchema(operandList.get(0), fromOrJoin, schema, config);
                }
            } else if (sqlNode.getKind() == IN
                    || sqlNode.getKind() == NOT_IN
                    || sqlNode.getKind() == AND
                    || sqlNode.getKind() == OR
                    || sqlNode.getKind() == LESS_THAN
                    || sqlNode.getKind() == GREATER_THAN
                    || sqlNode.getKind() == LESS_THAN_OR_EQUAL
                    || sqlNode.getKind() == GREATER_THAN_OR_EQUAL
                    || sqlNode.getKind() == EQUALS
                    || sqlNode.getKind() == NOT_EQUALS) {
                SqlBasicCall where = (SqlBasicCall) sqlNode;
                if (where.getOperandList().size() >= 2) {
                    for (int i = 0; i < where.getOperandList().size(); i++) {
                        addTableSchema(where.getOperandList().get(i), fromOrJoin, schema, config);
                    }
                }
            }
        } catch (Exception e) {
            DEException.throwException("使用 Calcite 进行语法分析发生了异常:" + e);
        }
    }

}
