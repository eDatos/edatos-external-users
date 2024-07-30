package es.gobcan.istac.edatos.external.users.core.service.criteria;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.arte.libs.grammar.domain.QueryPropertyRestriction;
import com.arte.libs.grammar.orm.jpa.criteria.AbstractCriteriaProcessor;
import com.arte.libs.grammar.orm.jpa.criteria.CriteriaProcessorContext;
import com.arte.libs.grammar.orm.jpa.criteria.OrderProcessorBuilder;
import com.arte.libs.grammar.orm.jpa.criteria.RestrictionProcessorBuilder;
import com.arte.libs.grammar.orm.jpa.criteria.converter.CriterionConverter;

import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.errors.CustomParameterizedExceptionBuilder;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;

@Component
public class FilterCriteriaProcessor extends AbstractCriteriaProcessor {

    public static final String ENTITY_FIELD_ID = validateFieldExists("id");
    public static final String ENTITY_FIELD_NAME = validateFieldExists("name");
    public static final String ENTITY_FIELD_USER = validateFieldExists("externalUser");
    public static final String ENTITY_FIELD_PERMALINK = validateFieldExists("permalink");
    public static final String ENTITY_FIELD_CREATED_DATE = validateFieldExists("createdDate");
    public static final String ENTITY_FIELD_LAST_MODIFIED_DATE = validateFieldExists("lastModifiedDate");
    public static final String ENTITY_FIELD_LAST_ACCESS_DATE = validateFieldExists("lastAccessDate");
    public static final String ENTITY_FIELD_NOTES = validateFieldExists("notes");

    private static final String TABLE_FIELD_EMAIL = ENTITY_FIELD_USER + ".email";
    private static final String TABLE_FIELD_NAME = ENTITY_FIELD_USER + ".name";
    private static final String TABLE_FIELD_SURNAME1 = ENTITY_FIELD_USER + ".surname1";
    private static final String TABLE_FIELD_SURNAME2 = ENTITY_FIELD_USER + ".surname2";

    public enum QueryProperty {
        ID,
        NAME,
        USER,
        USER_ID,
        EXTERNAL_USER_NAME,
        USER_DELETION_DATE,
        EMAIL,
        PERMALINK,
        CREATED_DATE,
        LAST_MODIFIED_DATE,
        LAST_ACCESS_DATE,
        NOTES,
        EXTERNAL_OPERATIONS
    }

    public FilterCriteriaProcessor() {
        super(FilterEntity.class);
    }

    /**
     * Tells if a field name exists for a class.
     * <p>
     * The reasoning behind this method is that it fails quickly if the declared
     * entity field name doesn't exists, either because it was changed, mispelled, or
     * another reason that may cause to fail silently in production otherwise, if not
     * correctly tested.
     * <p>
     * Be aware that to fail at startup this class need to be instantiated early.
     * Marking this as a {@code @Component} will delegate this to Spring.
     *
     * @implNote Limitations: this method DOES NOT check for nested entity properties
     *           like {@code user.login}.
     * @param fieldName the name of the field that is going to be checked.
     * @return the same field name passed as argument, if it exists.
     * @throws IllegalArgumentException if the field doesn't exists.
     */
    private static String validateFieldExists(String fieldName) {
        List<String> fieldNames = FieldUtils.getAllFieldsList(FilterEntity.class).stream().map(Field::getName).collect(Collectors.toList());
        if (fieldNames.contains(fieldName)) {
            return fieldName;
        } else {
            throw new IllegalArgumentException("Field name '" + fieldName + "' doesn't exists in '" + FilterCriteriaProcessor.class.toString() + "'");
        }
    }

    @Override
    public void registerProcessors() {
        // @formatter:off
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .longRestrictionProcessor()
                .withQueryProperty(QueryProperty.ID).sortable()
                .withEntityProperty(ENTITY_FIELD_ID)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .stringRestrictionProcessor()
                .withQueryProperty(QueryProperty.NAME).sortable()
                .withEntityProperty(ENTITY_FIELD_NAME)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .stringRestrictionProcessor()
                .withQueryProperty(QueryProperty.USER)
                .withCriterionConverter(new SqlCriterionBuilder())
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .longRestrictionProcessor()
                .withQueryProperty(QueryProperty.USER_ID)
                .withAlias(ENTITY_FIELD_USER, ENTITY_FIELD_USER)
                .withEntityProperty(ENTITY_FIELD_USER + ".id")
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .dateRestrictionProcessor()
                .withQueryProperty(QueryProperty.USER_DELETION_DATE)
                .withAlias(ENTITY_FIELD_USER, ENTITY_FIELD_USER)
                .withEntityProperty(ENTITY_FIELD_USER + ".deletionDate")
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .stringRestrictionProcessor()
                .withQueryProperty(QueryProperty.EMAIL)
                .withAlias(ENTITY_FIELD_USER, ENTITY_FIELD_USER)
                .withEntityProperty(ENTITY_FIELD_USER + ".email")
                .build());
        registerOrderProcessor(OrderProcessorBuilder
                .orderProcessor()
                .withQueryProperty(QueryProperty.EMAIL)
                .withAlias(ENTITY_FIELD_USER, ENTITY_FIELD_USER)
                .withEntityProperty(ENTITY_FIELD_USER + ".email")
                .build());
        registerOrderProcessor(OrderProcessorBuilder
                .orderProcessor()
                .withQueryProperty(QueryProperty.EXTERNAL_USER_NAME)
                .withAlias(ENTITY_FIELD_USER, ENTITY_FIELD_USER)
                .withEntityProperty(ENTITY_FIELD_USER + ".name")
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .stringRestrictionProcessor()
                .withQueryProperty(QueryProperty.PERMALINK).sortable()
                .withEntityProperty(ENTITY_FIELD_PERMALINK)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .dateRestrictionProcessor()
                .withQueryProperty(QueryProperty.CREATED_DATE).sortable()
                .withEntityProperty(ENTITY_FIELD_CREATED_DATE)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .dateRestrictionProcessor()
                .withQueryProperty(QueryProperty.LAST_MODIFIED_DATE).sortable()
                .withEntityProperty(ENTITY_FIELD_LAST_MODIFIED_DATE)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .dateRestrictionProcessor()
                .withQueryProperty(QueryProperty.LAST_ACCESS_DATE).sortable()
                .withEntityProperty(ENTITY_FIELD_LAST_ACCESS_DATE)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .stringRestrictionProcessor()
                .withQueryProperty(QueryProperty.NOTES).sortable()
                .withEntityProperty(ENTITY_FIELD_NOTES)
                .build());
        registerRestrictionProcessor(RestrictionProcessorBuilder
                .longRestrictionProcessor()
                .withQueryProperty(QueryProperty.EXTERNAL_OPERATIONS)
                .withCriterionConverter(new SqlCriterionBuilder())
                .build());
        // @formatter:on
    }

    private static class SqlCriterionBuilder implements CriterionConverter {

        @Override
        public Criterion convertToCriterion(QueryPropertyRestriction property, CriteriaProcessorContext context) {
            if (QueryProperty.USER.name().equalsIgnoreCase(property.getLeftExpression())) {
                return buildCriterion(property.getRightValue());
            }
            if (QueryProperty.EXTERNAL_OPERATIONS.name().equalsIgnoreCase(property.getLeftExpression())) {
                return buildExternalOperationsCriterion(property);
            }
            throw new CustomParameterizedExceptionBuilder().message(String.format("Parámetro de búsqueda no soportado: '%s'", property))
                    .code(ErrorConstants.QUERY_NO_SOPORTADA, property.getLeftExpression(), property.getOperationType().name()).build();
        }

        private Criterion buildExternalOperationsCriterion(QueryPropertyRestriction property) {
            // @formatter:off
            String sql = "{alias}.id IN "
                       + "(SELECT filter.id "
                       + "FROM tb_filters filter "
                       + "         JOIN tb_external_items ext_item on filter.external_operation_urn = ext_item.urn "
                       + "WHERE ext_item.id IN (" + String.join(",", property.getRightExpressions()) + ")"
                       + ")";
            // @formatter:on
            return Restrictions.sqlRestriction(sql);
        }

        private Criterion buildCriterion(String value) {
            // @formatter:off
            String query = "{alias}.id IN "
                         + "(SELECT filter.id "
                         + "FROM tb_filters filter "
                         + "         LEFT JOIN tb_external_users ext_user on filter.external_user_fk = ext_user.id "
                         + "WHERE ext_user.name ILIKE '" + value + "' "
                         + "   OR ext_user.surname1 ILIKE '" + value + "' "
                         + "   OR ext_user.surname2 ILIKE '" + value + "' "
                         + "   OR ext_user.email ILIKE '" + value + "')";
            // @formatter:on

            return Restrictions.sqlRestriction(query);
        }

    }
}
