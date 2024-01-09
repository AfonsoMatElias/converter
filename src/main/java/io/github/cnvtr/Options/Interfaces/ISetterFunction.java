package io.github.cnvtr.Options.Interfaces;

@FunctionalInterface
public interface ISetterFunction<ModelType, SetterType> {
    void accept(ModelType model, SetterType setterValue);
}
