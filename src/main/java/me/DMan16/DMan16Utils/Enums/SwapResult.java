package me.DMan16.DMan16Utils.Enums;

public enum SwapResult {
	SUCCESS(true),
	SUCCESS_OLD_NULL(true),
	SUCCESS_NEW_NULL(true),
	FAILURE(false);
	
	public final boolean success;
	
	SwapResult(boolean success) {
		this.success = success;
	}
}