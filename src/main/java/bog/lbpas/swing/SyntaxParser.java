package bog.lbpas.swing;

import bog.lbpas.view3d.utils.TextIcon;
import bog.lbpas.view3d.utils.print;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fife.ui.rsyntaxtextarea.parser.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.Gutter;

import javax.swing.*;
import java.awt.*;

public class SyntaxParser extends AbstractParser {
    private DefaultParseResult result;
    private ObjectMapper mapper;
    private Gutter gutter;

    public SyntaxParser(Gutter gutter) {
        this.result = new DefaultParseResult(this);
        this.mapper = new ObjectMapper();
        this.gutter = gutter;
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        result.clearNotices();

        try
        {
            String text = doc.getText(0, doc.getLength());

            switch (style) {
                case SyntaxConstants.SYNTAX_STYLE_JSON:
                case SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS:
                    validateJson(text, doc);
                    break;

                default:

                    break;
            }
        }catch (Exception e){print.stackTrace(e);}

        return result;
    }

    private void validateJson(String text, RSyntaxDocument doc) {
        try {
            mapper.readTree(text);
        }
        catch (JsonProcessingException e)
        {
            JsonLocation loc = e.getLocation();

            int line = loc.getLineNr() - 1;
            int column = loc.getColumnNr() - 1;

            int start = 0;
            int length = 0;

            try {
                int startOffset = doc.getDefaultRootElement().getElement(line).getStartOffset();
                start = startOffset + column;
                int endOffset = doc.getDefaultRootElement().getElement(line).getEndOffset();
                length = endOffset - start;

                if(length < 1)
                {
                    start = startOffset;
                    length = endOffset - start;
                }
                else if(length <= 2)
                {
                    start = start - 2;
                    length = endOffset - start;
                }
            } catch (Exception ex) {print.stackTrace(e);}

            String cleanMessage = e.getOriginalMessage();
            String displayMessage = String.format("%s (Line %d, Col %d)", cleanMessage, loc.getLineNr(), loc.getColumnNr());
            result.addNotice(new DefaultParserNotice(this, "JSON Error:\n" + displayMessage, line, start, length));
        }
    }
}