package example;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlSerializer;

public class HtmlUnitFastSerializer extends HtmlSerializer {
    public HtmlUnitFastSerializer() {
        setIgnoreMaskedElements( false );
    }

    @Override protected void appendDomNode( StringBuilder builder, DomNode domNode ) {
        final boolean block;
        if (domNode instanceof HtmlBody) {
            block = false;
        } else if (domNode instanceof HtmlElement) {
            HtmlElement htmlElement = (HtmlElement) domNode;
//            final String display = element.getWindow().getComputedStyle(element, null).getDisplay(true);
            String display = htmlElement.getDefaultStyleDisplay().value();
            block = "block".equals( display );
        } else
            block = false;

        if (block) builder.append( AS_TEXT_BLOCK_SEPARATOR );
        appendChildren( builder, domNode );
        if (block) builder.append( AS_TEXT_BLOCK_SEPARATOR );
    }
}
