package example;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.*;

public class CssBenchmark {
    int COUNT = 10;
    WebClient webClient;

    @BeforeTest
    private void initWebClient() {
        webClient = new WebClient( BrowserVersion.BEST_SUPPORTED );
        webClient.getOptions().setThrowExceptionOnScriptError( false );
        webClient.getOptions().setThrowExceptionOnFailingStatusCode( false );
        webClient.getOptions().setJavaScriptEnabled( false );
        webClient.getOptions().setCssEnabled( true );
    }

    private HtmlPage parseHtml( String html ) {
        WebClient client = webClient;
        try {
            StringWebResponse response = new StringWebResponse( html, new URL( "http://dummy.com/" ) );
            return HTMLParser.parseHtml( response, client.getCurrentWindow() );
        } catch (Exception ex) {
            throw new RuntimeException( ex );
        }
    }

    @Test
    public void benchmarkLargeCss() {
        File cssFile = new File( "src/test/resources/big.css" );
        assertTrue( cssFile.exists() );

        String html = "" +
                "<head> <link rel='stylesheet' href='" + cssFile.toURI().toString() + "'></head>" +
                "<body>" +
                "   <div>content</div>" +
                "</body>";

        long timeAccum = 0;
        for (int i = 0; i < COUNT; ++i) {
            HtmlPage doc = parseHtml( html );
            long startTime = System.nanoTime();
            String content = doc.asText();
            timeAccum += System.nanoTime() - startTime;
            assertEquals( content, "content" );
        }
        System.out.println( String.format( "Elapsed %.2f ms", ((double) timeAccum) / COUNT / 1_000_000 ) );
    }

    @Test
    public void benchmarkWithoutStyles() {
        String htmlWithoutStyles = "" +
                "<body>" +
                "   <div>content</div>" +
                "</body>";
        long timeAccum = 0;
        for (int i = 0; i < COUNT; ++i) {
            HtmlPage doc = parseHtml( htmlWithoutStyles );
            long startTime = System.nanoTime();
            String content = doc.asText();
            timeAccum += System.nanoTime() - startTime;
            assertEquals( content, "content" );
        }
        System.out.println( String.format( "Elapsed %.2f ms", ((double) timeAccum) / COUNT / 1_000_000 ) );
    }

    @Test
    public void benchmarkCustomSerializer() {
        File cssFile = new File( "src/test/resources/big.css" );
        assertTrue( cssFile.exists() );

        String html = "" +
                "<head> <link rel='stylesheet' href='" + cssFile.toURI().toString() + "'></head>" +
                "<body>" +
                "   <div>content</div>" +
                "</body>";
        HtmlUnitFastSerializer fastSerializer = new HtmlUnitFastSerializer();
        long timeAccum = 0;
        for (int i = 0; i < COUNT; ++i) {
            HtmlPage doc = parseHtml( html );
            long startTime = System.nanoTime();
            String content = fastSerializer.asText( doc );
            timeAccum += System.nanoTime() - startTime;
            assertEquals( content, "content" );
        }
        System.out.println( String.format( "Elapsed %.2f ms", ((double) timeAccum) / COUNT / 1_000_000 ) );
    }
}
