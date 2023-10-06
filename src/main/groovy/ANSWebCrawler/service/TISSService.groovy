package ANSWebCrawler.service

import ANSWebCrawler.domain.TISS

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovyx.net.http.optional.Download
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.format.DateTimeFormatter
import java.time.LocalDate

import static groovyx.net.http.HttpBuilder.configure

class TISSService {

    Document populateDocument(String url) {
        return Jsoup.connect(url).get()
    }

    void downloadFile(String url, String folder) {
        configure {
            request.uri = url
        }.get {
            Download.toFile(
                    delegate,
                    new File("./Downloads/${folder}/${url.split("/").last()}"))
        }
    }

    void writeFile(List<TISS> versionHistory) {
        try {
            String filePath = "./src/main/resources/versionHistory.json"
            Gson gson = new GsonBuilder().setPrettyPrinting().create()
            gson.toJson(versionHistory, new FileWriter(filePath))
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    void getTISSFiles() {
        getCommunicationComponentFile()
        getOrganizationalComponentFile()
        getContentAndStructureComponentFile()
        getHealthConceptRepresentationComponentFile()
        getSecurityAndPrivacyComponentFile()
        getErrorTableFile()
    }

    Document getTISSContent() {
        String url = "https://www.gov.br/ans/pt-br"
        Document doc = populateDocument(url)

        doc.getElementsByTag("img").each {img ->
            if (img.attr("alt").equals("Espaço do Prestador")) {
                url = img.parent().attr("href")
                doc = populateDocument(url)
            }
        }

        doc.getElementsByTag("a").each {span ->
            if (span.text().equals("TISS - Padrão para Troca de Informação de Saúde Suplementar")) {
                url = span.attr("href")
                doc = populateDocument(url)
            }
        }

        return doc
    }

    Document getActualTISSContent() {
        String url = ""
        Document doc = getTISSContent()

        doc.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains("Padrão TISS – Versão")) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                doc = populateDocument(url)
            }
        }

        return doc
    }

    void getErrorTableFile() {
        String url = ""
        Document doc = getTISSContent()

        doc.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains("Padrão TISS – Tabelas Relacionadas")) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                doc = populateDocument(url)
            }
        }

        doc.getElementsByTag("h2").each {h2 ->
            if (h2.text().equals("Tabela de erros no envio para a ANS")) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Tabela de Erros no Envio")
    }

    void getCommunicationComponentFile() {
        String url = ""
        Document doc = getActualTISSContent()

        doc.getElementsByTag("td").each {td ->
            if (td.text().equals("Componente de Comunicação")) {
                url = td.lastElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Componente de Comunicacao")
    }

    void getOrganizationalComponentFile() {
        String url = ""
        Document doc = getActualTISSContent()

        doc.getElementsByTag("td").each {td ->
            if (td.text().equals("Componente Organizacional")) {
                url = td.lastElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Componente Organizacional")
    }

    void getContentAndStructureComponentFile() {
        String url = ""
        Document doc = getActualTISSContent()

        doc.getElementsByTag("td").each {td ->
            if (td.text().equals("Componente de Conteúdo e Estrutura")) {
                url = td.lastElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Componente de Conteúdo e Estrutura")
    }

    void getHealthConceptRepresentationComponentFile() {
        String url = ""
        Document doc = getActualTISSContent()

        doc.getElementsByTag("td").each {td ->
            if (td.text().contains("Componente de Conteúdo e Estrutura")) {
                url = td.lastElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Componente de Representação de Conceitos em Saúde")
    }

    void getSecurityAndPrivacyComponentFile() {
        String url = ""
        Document doc = getActualTISSContent()

        doc.getElementsByTag("td").each {td ->
            if (td.text().equals("Componente de Segurança e Privacidade")) {
                url = td.lastElementSibling()
                        .firstElementChild()
                        .attr("href")
            }
        }

        downloadFile(url, "Componente de Segurança e Privacidade")
    }

    void getVersionHistoryFile() {
        String url = ""
        Document doc = getTISSContent()

        doc.getElementsByTag("h2").each {h2 ->
            if (h2.text().contains("Padrão TISS – Histórico das versões")) {
                url = h2.nextElementSibling()
                        .firstElementChild()
                        .attr("href")
                doc = populateDocument(url)
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        LocalDate initialDate = LocalDate.parse("2016-01-01")
        List<TISS> rows = new ArrayList<>()

        if (doc != null) {
            int id = 1

            doc.select('tr').drop(1).each {tr ->
                List<String> row = new ArrayList<>()

                row.add(id)
                tr.select('td').take(3).each {td ->
                    row.add(td.text())
                }

                LocalDate publication = LocalDate.parse(row.get(2), formatter)
                LocalDate beginningOfTerm = LocalDate.parse(row.get(3), formatter)

                if (beginningOfTerm >= initialDate) {
                    rows.add(new TISS(row.get(0) as int, row.get(1), publication, beginningOfTerm))
                }
                id++
            }
        }

        writeFile(rows)
    }



}
