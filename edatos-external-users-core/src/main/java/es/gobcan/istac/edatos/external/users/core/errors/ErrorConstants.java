package es.gobcan.istac.edatos.external.users.core.errors;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
    public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
    public static final String ERR_DATA_CONSTRAINT = "error.dataConstraintViolation";

    /**********
     * USER
     *********/
    public static final String USUARIO_EXISTE = "error.usuario-existe";
    public static final String USUARIO_NO_VALIDO = "error.userManagement.usuario-no-valido";

    /**********
     * ENTITY
     *********/
    public static final String ENTIDAD_NO_ENCONTRADA = "error.entidad-no-encontrada";

    /**********
     * QUERY
     *********/
    public static final String QUERY_NO_SOPORTADA = "error.query-no-soportada";

    // QUARTZ
    public static final String QUARTZ_JOB_EXECUTION_ERROR = "error.quartz.jobExecutionError";

    private ErrorConstants() {
    }

}
