package calcite.test.tpcds;

import calcite.test.tpcds.utils.PlannerUtil;
import calcite.test.tpcds.utils.SchemaSpec;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.tpcds.TpcdsSchema;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.fun.SqlLibrary;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * This class utilizes CalCite SQL parser to parse SQL query (TPC-DS queries) into relational expressions.
 */
public class SQL2Log {
    private final String sqlQuery;
    private final SchemaPlus schema;
    private final SqlDialect dialect;
    private final SqlParser.Config parserConfig;
    private final Set<SqlLibrary> librarySet;
    private @Nullable
    final Function<RelBuilder, RelNode> relFn;
    private final List<Function<RelNode, RelNode>> transforms;
    private final UnaryOperator<SqlToRelConverter.Config> config;

    /**
     * Constructor
     **/
    public SQL2Log(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        final SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        this.schema = rootSchema.add(SchemaSpec.TPCDS.schemaName, new TpcdsSchema(5));
        this.dialect = SqlDialect.DatabaseProduct.CALCITE.getDialect();
        this.parserConfig = SqlParser.Config.DEFAULT;
        this.librarySet = ImmutableSet.of();
        this.relFn = null;
        this.transforms = ImmutableList.of();
        this.config = UnaryOperator.identity();
    }


    /**
     * Get optimized logical plan using Calcite rewrite rules for TPC-DS queries
     *
     * @return optimized relNode
     */
    public RelNode getLogOptimized()  {
        final Planner planner = PlannerUtil.getPlannerDefaultTPCDSQueries();
        SqlNode parse = null;
        SqlNode validate =null;
        RelNode root =null;
        try {
            parse = planner.parse(sqlQuery);
            validate  = planner.validate(parse);
            root = planner.rel(validate).rel;
        } catch (SqlParseException | ValidationException | RelConversionException e) {
            e.printStackTrace();
        }
        final RelOptPlanner plannerOptimized = root.getCluster().getPlanner();
        plannerOptimized.clear();
        for (RelOptRule rule : PlannerUtil.getRuleSet()) {
            plannerOptimized.addRule(rule);
        }
        final Program program = Programs.of(RuleSets.ofList(plannerOptimized.getRules()));
        return program.run(plannerOptimized, root, root.getTraitSet().replace(EnumerableConvention.INSTANCE),
                ImmutableList.of(), ImmutableList.of());
    }

    /**
     * Get optimized logical plan using Calcite rewrite rules for TPC-DS queries (Volcano)
     *
     * @return optimized relNode
     * @throws SqlParseException
     * @throws ValidationException
     * @throws RelConversionException
     */
    public RelNode getLogOptimizedVolcano() throws Exception {
        final Planner planner = PlannerUtil.getPlannerDefaultTPCDSQueries();
        SqlNode parse = null;
        SqlNode validate =null;
        RelNode root =null;
        try {
            parse = planner.parse(sqlQuery);
            validate  = planner.validate(parse);
            root = planner.rel(validate).rel;
        } catch (SqlParseException | ValidationException | RelConversionException e) {
            e.printStackTrace();

        }
        VolcanoPlanner CBOplanner = (VolcanoPlanner) root.getCluster().getPlanner();
        CBOplanner.clear();
        CBOplanner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        for (RelOptRule rule : PlannerUtil.getRuleSet()) {
            CBOplanner.addRule(rule);
        }
        Program program = Programs.of(RuleSets.ofList(CBOplanner.getRules()));
        program.run(CBOplanner, root, root.getTraitSet().replace(EnumerableConvention.INSTANCE),
                ImmutableList.of(), ImmutableList.of());

        return  CBOplanner.chooseDelegate().findBestExp();
    }
}
