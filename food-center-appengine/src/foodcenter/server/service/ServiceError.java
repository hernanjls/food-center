package foodcenter.server.service;

public class ServiceError extends Error
{
    /**
     * 
     */
    private static final long serialVersionUID = 6363376068997486837L;
    
    public static final String UNKNOWN_ISSUE = "Unknown issue";
    public static final String DATABASE_ISSUE = "Database issue! please contact Administrator";
    public static final String PREMISSION_DENIED = "Premission denied!, your attempt has been logged";
    public static final String PREMISSION_DENIED_MODIFY_ORDER = "Premission denied to modify Order!, your attempt has been logged";
    public static final String INVALID_NULL_INPUT = "Invalid null input";
    public static final String INVALID_REST_BRANCH_ID = "Invalid Restaurant Branch ID: ";
    public static final String INVALID_REST_ID = "Invalid Restaurant ID: ";
    public static final String INVALID_COMP_BRANCH_ID = "Invalid Company Branch ID: ";
    public static final String INVALID_COMP_ID = "Invalid Company ID: ";
    public static final String INVALID_ORDER_ID = "Invalid Order ID: ";
    public static final String COMPANY_NOT_ASSOCIATED_TO_BRANCH = "Company branch is not associated to Company";
    public static final String DEFAULT_RESTS_NOT_FOUND = "Default restaurants not found...";
    public static final String DEFAULT_COMPS_NOT_FOUND = "Default companies not found...";
    public static final String REST_PATTERN_NOT_FOUND = "Can't find Restaurants, please use different pattern ...";
    public static final String COMP_PATTERN_NOT_FOUND = "Can't find Companies, please use different pattern ...";
    public static final String USER_COMPNAY_NOT_FOUND = "Can't find User's company: ";

    public ServiceError()
    {
        this(UNKNOWN_ISSUE);
    }

    public ServiceError(String message)
    {
        super(message);
    }

    public ServiceError(Throwable cause)
    {
        super(cause);
    }

    public ServiceError(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceError(String message,
                        Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
