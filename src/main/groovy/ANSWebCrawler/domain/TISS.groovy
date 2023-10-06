package ANSWebCrawler.domain

import groovy.transform.TupleConstructor

import java.time.LocalDate

@TupleConstructor
class TISS {

    int id
    String competency
    LocalDate publication
    LocalDate beginningOfTerm

}
