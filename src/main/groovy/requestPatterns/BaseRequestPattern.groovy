package requestPatterns
/**
 * Created by ryabnat on 2/25/2016.
 */
import static requestPatterns.RequestPatternUtils.checkAndReplace

class BaseRequestPattern<R> {
    def static Map nullPattern(R searchCriteria) {
        def Map searchMap = searchCriteria.properties
                .findAll { key, value -> !"class".equals(key) }
                .each { it ->
            it.value = ("sp_id").equals(it.key)||("projections").equals(it.key)|| ("parent_pt_id").equals(it.key) ? requestPatterns.RequestPatternUtils.checkAndReplace(it.value, [], null) : requestPatterns.RequestPatternUtils.checkAndReplace(it.value, "", null)
        }
        searchMap
    }

    def static Map emptyPattern(R searchCriteria) {
        def Map searchMap = searchCriteria.properties
                .findAll { key, value -> !"class".equals(key) }
        searchMap
    }

    def static Map absentPattern(R searchCriteria) {
        def Map searchMap = searchCriteria.properties
                .findAll { key, value -> !"class".equals(key) && !"".equals(value) && ![].equals(value) }
        searchMap
    }
}
