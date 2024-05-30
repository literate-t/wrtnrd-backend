package io.taetae.wrtnrd.util;

public class Constant {

  // common
  public static final String INTERNAL_SERVER_ERROR = "Internal server error";
  public static final String NO_PARAM_FROM_REQUEST = "Required data is empty";
  public static final String USER_ID_EMPTY = "User id is empty";

  // token
  public static final String ACCESS_TOKEN = "ac";
  public static final String REFRESH_TOKEN = "rf";

  // mypage-password
  public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully";
  public static final String PASSWORD_CHECKED_SUCCESSFULLY = "Password checked successfully";
  public static final String PASSWORD_CHECK_FAILURE= "Password check failure";
  public static final String ACCESS_TOKEN_MISSING = "Access token is missing";
  public static final String USER_NOT_FOUND_FROM_ACCESS_TOKEN = "User not found from access token";

  // mypage-author
  public static final String AVAILABLE_AUTHOR = "Author is available";
  public static final String AUTHOR_CHECK_FAILURE = "Author check failure";
  public static final String AUTHOR_UPDATED = "Author is updated";
  public static final String AUTHOR_NOT_UPDATED = "Author is not updated";

  // revoke tokens
  public static final String REVOKE_ALL_PREVIOUS_TOKENS = "revoke all previous user token";
}
