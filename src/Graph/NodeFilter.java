package Graph;

/**
 * Created by IntelliJ IDEA.
 * User: adam
 * Date: 13/03/2009
 * Time: 1:53:34 PM
 * This interface allows you to filter a search through the graph for specific conditions.
 */
public interface NodeFilter<NodeType> {
    public boolean matches(NodeType target);
}
