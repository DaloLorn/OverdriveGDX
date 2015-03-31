/**
 * 
 */
/**
 * Bug with Beanshell: overriding methods that have primitive parameters in scripts
 * causes a java.lang.VerifyError due to mismatched types, since Beanshell tries
 * to compare primitive types with their wrappers.
 * 
 * Workaround is to use wrapper objects for libgdx's events, that take Objects as
 * parameters instead of primitive types.
 * 
 * Alternatively, since BeanShell appears to not be supported anymore, fix the bug
 * ourselves and ship the game with our custom .jar.
 */
package com.ftloverdrive.bsh;