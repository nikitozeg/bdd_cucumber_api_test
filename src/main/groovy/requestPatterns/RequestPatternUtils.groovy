package requestPatterns

class RequestPatternUtils {

    static def checkAndReplace(value, pattern, newValue) {
        pattern.equals(value) ? newValue : value
    }
}
