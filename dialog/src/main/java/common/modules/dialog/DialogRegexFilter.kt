package common.modules.dialog

/**
 * Dialog 정규 표현식 관련 Enum Class
 */
enum class DialogRegexFilter(val regex:String) {
    DIALOG_REGEX_TYPE_NUMBER("^[+-]?[0-9]*\$"),
    DIALOG_REGEX_TYPE_POSITIVE_NUMBER("^[0-9]*\$"),
    DIALOG_REGEX_TYPE_FLOAT("^[+-]?[0-9\\.]*\$"),
    DIALOG_REGEX_TYPE_ALPHABET("^([a-zA-Z])*\$"),
    DIALOG_REGEX_TYPE_EMAIL("[a-zA-Z0-9\\\\+\\\\.\\\\_\\\\%\\\\-\\\\+]{1,256}\"+\n"+"\"\\\\@\" +\n"+"\"[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,64}\" +\n"+"\"(\" +\n" +"\"\\\\.\" +\n"+"\"[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,25}\" +\n"+"\")+"),
    DIALOG_REGEX_TYPE_PHONE("\\d{3}-\\d{4}-\\d{4}"),
    DIALOG_REGEX_TYPE_PASSWORD("^.*(?=^.{8,}\$)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\\\~!@#\$%^&*()_\\-+=|\"'?/<>,.;:]).*\$")
}