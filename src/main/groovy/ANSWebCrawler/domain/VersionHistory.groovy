package ANSWebCrawler.domain

import groovy.transform.TupleConstructor
import java.time.LocalDate

@TupleConstructor
class VersionHistory {

    int id
    String competency
    LocalDate publication
    LocalDate beginningOfTerm

}
