package hu.bme.msc.onlab.trhandlertool.interf;

@FunctionalInterface
public interface SupplierWithException<T> {
	T get() throws Exception;
}
