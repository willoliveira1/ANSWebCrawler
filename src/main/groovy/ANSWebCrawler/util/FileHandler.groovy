package ANSWebCrawler.util

import ANSWebCrawler.domain.TISSVersionHistory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static groovyx.net.http.HttpBuilder.configure

class FileHandler {

    static void writeFile(List<TISSVersionHistory> versionHistory) {
        try {
            String filePath = Paths.FILE_PATH
            Gson gson = new GsonBuilder().setPrettyPrinting().create()
            gson.toJson(versionHistory, new FileWriter(filePath))
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    static Document populateDocument(String url) {
        return Jsoup.connect(url).get()
    }

    static void downloadFile(String url, String folder) {
        configure {
            request.uri = url
        }.get {
            Download.toFile(
                    delegate,
                    new File(
                            "${Paths.DOWNLOADS_PATH}/" +
                            "${folder}/" +
                            "${url.split("/").last()}"
                    )
            )
        }
    }

}
