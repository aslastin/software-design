package searcher.builder;

import searcher.Searcher;

public interface SearcherBuilder {
    Searcher build(Class<? extends Searcher> clazz);
}
