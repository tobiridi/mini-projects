package be.tobiridi.passwordsecurity.data.datasources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import be.tobiridi.passwordsecurity.data.datasources.local.LocalDataSource;

/**
 * Provider for all data source, centralize data source references.
 * <br/>
 * provide methods to add, retrieve and remove data sources.
 */
public final class DataSourceProvider {
    private static DataSourceProvider INSTANCE;
    private final Set<LocalDataSource> localDataSources;

    private DataSourceProvider() {
        this.localDataSources = new HashSet<>();
    }

    public static DataSourceProvider getProvider() {
        if (INSTANCE == null)
            INSTANCE = new DataSourceProvider();
        return INSTANCE;
    }

    public boolean addDataSource(LocalDataSource dataSource) {
        return this.localDataSources.add(dataSource);
    }

    public boolean removeDataSource(LocalDataSource dataSource) {
        return this.localDataSources.remove(dataSource);
    }

    /**
     * Retrieve a {@link LocalDataSource} object who already registered in the provider.
     * @param dataSourceClass Any class implementing a {@link LocalDataSource}.
     * @return An instance of {@link LocalDataSource} or {@code null} if not found.
     */
    public <T extends LocalDataSource> T getLocalDataSource(Class<T> dataSourceClass) {
        if(this.localDataSources.isEmpty())
            return null;

        LocalDataSource source = this.localDataSources.stream()
                .filter(ds -> ds.getClass() == dataSourceClass)
                .findFirst()
                .orElse(null);

        return dataSourceClass.cast(source);
    }

    public Iterable<LocalDataSource> getAllLocalDataSources() {
        return new ArrayList<>(this.localDataSources);
    }

    public void clearAllDataSources() {
        this.localDataSources.clear();
    }
}
