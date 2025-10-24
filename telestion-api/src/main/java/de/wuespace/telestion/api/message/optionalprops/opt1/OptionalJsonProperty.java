package de.wuespace.telestion.api.message.optionalprops.opt1;

public @interface OptionalJsonProperty {
	byte defaultByte() default -1;
	short defaultShort() default -1;
	int defaultInt() default -1;
	long defaultLong() default -1;
	float defaultFloat() default -1.0f;
	double defaultDouble() default -1.0;
	char defaultChar() default 0x0;
	boolean defaultBool() default false;
	String defaultString() default "";
}
