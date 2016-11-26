/* No copyright, no warranty, only code. 
 * This file was created on 23 Nov 2014. It was a good day.
 */
package org.mettascript.library;

import java.lang.annotation.*;

/**
 *
 * @author Zak Fenton
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Exposed {
	String[] value() default {};
}
