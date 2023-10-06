package ANSWebCrawler

import ANSWebCrawler.service.TISSService

class Main {

    static void main(String[] args) {

        TISSService tissService = new TISSService()

        tissService.getTISSFiles()

        tissService.getVersionHistoryFile()

    }

}