package com.imcode.oeplatform.flowengine.populators.dao;

import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.populators.BaseStringPopulator;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by vitaly on 25.09.15.
 */
public final class DaoPopulatorFactory {
    private DaoPopulatorFactory() {
    }

    private static class LambdaDelegationBeanStringPopulator<T extends Serializable> extends BaseStringPopulator<T> implements DaoPopulator<T> {
        private final Function<String, T> fromStringmapper;
        private final Function<Object, String> toStringmapper;
        private final Class<? extends T> clazz;

        public LambdaDelegationBeanStringPopulator(Class<? extends T> clazz, Function<String, T> fromStringmapper, Function<Object, String> toStringmapper) {
            Objects.requireNonNull(clazz);
            Objects.requireNonNull(fromStringmapper);
            Objects.requireNonNull(toStringmapper);

            this.fromStringmapper = fromStringmapper;
            this.toStringmapper = toStringmapper;
            this.clazz = clazz;
        }

        @Override
        protected boolean validateDefaultFormat(String value) {
            return true;
        }

        @Override
        public T getValue(String value) {
            return fromStringmapper.apply(value);
        }

        @Override
        public void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException {
            query.setString(paramIndex, toStringmapper.apply(bean));
        }

        @Override
        public Class<? extends T> getType() {
            return clazz;
        }
    }

    public static <T extends Serializable> DaoPopulator<T> get(Class<? extends T> clazz, Function<String, T> fromStringmapper, Function<Object, String> toStringmapper) {
        return new LambdaDelegationBeanStringPopulator<>(clazz, fromStringmapper, toStringmapper);
    }

    public static <T extends Serializable> DaoPopulator<T> get(Class<? extends T> clazz) {
        return get(clazz, DaoPopulator::fromBase64String, DaoPopulator::toBase64String);
    }
}
