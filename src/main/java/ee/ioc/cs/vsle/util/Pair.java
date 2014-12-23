package ee.ioc.cs.vsle.util;

public class Pair<F, S> {

    private F first;
    private S second;
    
    public Pair( F first, S second ) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public F getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public S getSecond() {
        return second;
    }

    /**
     * @param first the first to set
     */
    public Pair<F, S> setAtFirst( F first ) {
        return new Pair<F, S>( first, second );
    }

    /**
     * @param second the second to set
     */
    public Pair<F, S> setAtSecond( S second ) {
        return new Pair<F, S>( first, second );
    }
    
}
