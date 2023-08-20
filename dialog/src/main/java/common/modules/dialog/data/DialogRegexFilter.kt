package common.modules.dialog.data

/**
 * Dialog 정규 표현식 관련 Enum Class
 */
enum class DialogRegexFilter(val filter:String, val errorMessage : String) {
    DIALOG_REGEX_TYPE_NUMBER("^[+-]?[0-9]*\$","숫자만 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_POSITIVE_NUMBER("^[0-9]*\$","양수만 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_FLOAT("^[+-]?[0-9\\.]*\$","실수만 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_ALPHABET("^([a-zA-Z])*\$","영문만 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_EMAIL("[a-zA-Z0-9\\\\+\\\\.\\\\_\\\\%\\\\-\\\\+]{1,256}\"+\n"+"\"\\\\@\" +\n"+"\"[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,64}\" +\n"+"\"(\" +\n" +"\"\\\\.\" " +
            "+\n"+"\"[a-zA-Z0-9][a-zA-Z0-9\\\\-]{0,25}\" +\n"+"\")+","이메일 양식에 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_PHONE("\\d{3}-\\d{4}-\\d{4}","휴대전화 번호 양식에 알맞게 입력해주세요."),
    DIALOG_REGEX_TYPE_PASSWORD("^.*(?=^.{8,}\$)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\\\~!@#\$%^&*()_\\-+=|\"'?/<>,.;:]).*\$","비밀번호 양식에 알맞게 입력해주세요.")
}