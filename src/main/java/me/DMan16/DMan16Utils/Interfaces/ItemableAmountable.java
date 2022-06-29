package me.DMan16.DMan16Utils.Interfaces;

public interface ItemableAmountable<V extends Itemable<V> & Amountable<V>> extends Itemable<V>,Amountable<V> {}