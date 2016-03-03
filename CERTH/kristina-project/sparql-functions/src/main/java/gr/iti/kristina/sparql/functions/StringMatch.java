package gr.iti.kristina.sparql.functions;

import gr.iti.kristina.sparql.functions.lib.StringSimilarity;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 *
 * @author gmeditsk
 */
public class StringMatch implements Function {

    public static final String NAMESPACE = "http://www.kristina.eu/sparql/functions/";

    @Override
    public String getURI() {
        return NAMESPACE + "stringMatch";
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... args) throws ValueExprEvaluationException {
        if (args.length != 2) {
            throw new ValueExprEvaluationException("Custom function stringMatch requires 2 arguments");
        }

        String kb_value = args[0].stringValue();
        String input = args[1].stringValue();
        //boolean matches = kb_value.matches("(?i)\\b" + input + "\\b");

        return valueFactory.createLiteral(StringSimilarity.similarity(kb_value, input));
    }

//    public static void main(String[] args) {
//        String kb_value = "aGe";
//        String input = "age";
//        boolean matches = kb_value.matches("(?i)\\b" + input + "\\b");
//        System.out.println("\\b" + input + "\\b");
//        System.out.println(matches);
//    }
}
