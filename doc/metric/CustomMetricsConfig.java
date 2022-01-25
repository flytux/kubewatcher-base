@Configuration
@ConditionalOnClass(name="io.micrometer.core.instrument.MeterRegistry")
protected static class CustomMetricsConfig {

    private static final String JVM_METRIC_NAME = "jvm.threads.deadlocked";
    private static final String JVM_METRIC_DESC = "In deadlock waiting to acquire object monitors or ownable synchronizers";

    private static final String CPU_METRIC_NAME = "process.cpu.seconds.total";
    private static final String CPU_METRIC_DESC = "Total user and system CPU time spent in seconds";

    private static final double NANOSECONDS_PER_SECOND = 1E9;
    private static final String CPU_METHOD_NAME = "getProcessCpuTime";
    private static final List<String> CPU_CLASS_NAMES = ImmutableList.of("com.ibm.lang.management.OperatingSystemMXBean", "com.sun.management.OperatingSystemMXBean");

    private final ThreadMXBean mxBean;
    private final OperatingSystemMXBean osBean;

    @Nullable private final Method osMethod;
    @Nullable private final Class<?> osClazz;

    protected CustomMetricsConfig() {

        this.mxBean = ManagementFactory.getThreadMXBean();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.osClazz = findMXBeanClass();
        this.osMethod = findMXBeanMethod();
    }

    @Bean
    @ConditionalOnMissingBean
    public JvmInfoMetrics jvmInfoMetrics() {
        return new JvmInfoMetrics();
    }

    @Bean
    public MeterBinder jvmDeadlockThreadsMetrics() {
        return mr -> Gauge.builder(JVM_METRIC_NAME, mxBean, v -> calculate(v.findDeadlockedThreads())).description(JVM_METRIC_DESC).tags(ImmutableList.of()).register(mr);
    }

    @Bean
    public MeterBinder processCpuSecondTotalMetrics() {
        return mr -> FunctionCounter.builder(CPU_METRIC_NAME, osBean, m -> executeMethod()).tags(ImmutableList.of()).description(CPU_METRIC_DESC).register(mr);
    }

    private Double calculate(@Nullable final long[] source) {
        return Double.valueOf(Optional.ofNullable(source).map(c -> c.length).orElse(0));
    }

    @Nullable
    private Class<?> findMXBeanClass() {

        for (final String name : CPU_CLASS_NAMES) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException e) {
                // No Operation Here
            }
        }

        return null;
    }

    @Nullable
    private Method findMXBeanMethod() {

        if (Objects.isNull(osClazz)) { return null; }

        try {
            osClazz.cast(osBean);
            return osClazz.getDeclaredMethod(CPU_METHOD_NAME);
        } catch (final ClassCastException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private double executeMethod() {

        if (Objects.isNull(osMethod)) { return Double.NaN; }

        try {
            return Optional.ofNullable(osMethod.invoke(osBean)).map(v -> ((Long)v) / NANOSECONDS_PER_SECOND).orElse(Double.NaN);
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Double.NaN;
        }
    }
}
