package com.kubeworks.watcher.data.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kubernetes.client.custom.Quantity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClusterPodUsage {

    @Builder
    private ClusterPodUsage(String application, String namespace, Quantity cpu, Quantity memory, LocalDateTime createTime) {
        this.application = application;
        this.namespace = namespace;
        this.cpu = cpu;
        this.maxCpu = cpu;
        this.memory = memory;
        this.maxMemory = memory;
        this.createTime = createTime;
    }

    String application;
    String namespace;
    int maxPodCount;
    int podCount = 1;
    Quantity maxCpu;
    Quantity avgCpu;
    Quantity cpu;
    Quantity maxMemory;
    Quantity avgMemory;
    Quantity memory;
    LocalDateTime createTime;

    @JsonIgnore
    public BigDecimal getMaxCpuNumber() {
        return maxCpu.getNumber();
    }

    public void setMaxCpu(BigDecimal maxCpu) {
        this.maxCpu = new Quantity(maxCpu, Quantity.Format.DECIMAL_SI);;
    }

    @JsonIgnore
    public BigDecimal getAvgCpuNumber() {
        return getAvgCpu().getNumber();
    }

    public Quantity getAvgCpu() {
        if (avgCpu == null && cpu != null) {
            BigDecimal calcAvgCpu = cpu.getNumber().divide(new BigDecimal(podCount), 9, RoundingMode.HALF_UP);
            return new Quantity(calcAvgCpu, Quantity.Format.DECIMAL_SI);
        }
        return avgCpu;
    }

    public void setAvgCpu(BigDecimal avgCpu) {
        this.avgCpu = new Quantity(avgCpu, Quantity.Format.DECIMAL_SI);;
    }

    @JsonIgnore
    public BigDecimal getMaxMemoryNumber() {
        return maxMemory.getNumber();
    }

    public void setMaxMemory(BigDecimal maxMemory) {
        this.maxMemory = new Quantity(maxMemory, Quantity.Format.BINARY_SI);
    }

    @JsonIgnore
    public BigDecimal getAvgMemoryNumber() {
        return getAvgMemory().getNumber();
    }

    public Quantity getAvgMemory() {
        if (avgMemory == null && memory != null) {
            BigDecimal calcAvgMemory = memory.getNumber().divide(new BigDecimal(podCount), 0, RoundingMode.HALF_UP);
            return new Quantity(calcAvgMemory, Quantity.Format.BINARY_SI);
        }
        return avgMemory;
    }

    public void setAvgMemory(BigDecimal avgMemory) {
        this.avgMemory = new Quantity(avgMemory, Quantity.Format.BINARY_SI);
    }

    public void addCpu(Quantity cpu) {
        if (cpu.getNumber().compareTo(this.maxCpu.getNumber()) > 0) {
            this.maxCpu = cpu;
        }
        this.cpu = new Quantity(this.cpu.getNumber().add(cpu.getNumber()), this.cpu.getFormat());
    }

    public void addMemory(Quantity memory) {
        if (memory.getNumber().compareTo(this.maxMemory.getNumber()) > 0) {
            this.maxMemory = memory;
        }
        this.memory = new Quantity(this.memory.getNumber().add(memory.getNumber()), this.memory.getFormat());
    }

    public void addPodCount(int podCount) {
        this.podCount += podCount;
    }

}
