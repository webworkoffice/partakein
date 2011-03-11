package in.partake.model.dto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractPartakeModelTest<T extends PartakeModel<?>> {
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * freezeしてから破壊的メソッドを実行するとUnsupportedOperationExceptionが投げられることを保証する。
	 * freezeしていない時に正しく実行できることの保証は、各サブクラスのテストが担う。
	 * @author skypencil(@eller86)
	 */
	@Test
	public final void testToCheckFrozen() {
		final PartakeModel<?> model = createModel().freeze();

		for (Method method : model.getClass().getMethods()) {
			if (!checkDestructiveMethod(method) || method.getDeclaringClass().equals(Object.class)) continue;
			logger.debug(String.format("Test for %s#%s starts.", model.getClass().getName(), method.getName()));
			Object[] args = createArgsFor(method);

			try {
				method.invoke(model, args);
				Assert.fail(method.getName() + " should throw UnsupportedOperationException but doesn't throw.");
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			} catch (InvocationTargetException e) {
				if (!e.getTargetException().getClass().equals(UnsupportedOperationException.class)) {
					throw new AssertionError(e);
				}
			}
		}
	}

	private Object[] createArgsFor(final Method m) {
		final Type[] types = m.getGenericParameterTypes();
		final Object[] result = new Object[types.length];
		for (int i = 0; i < types.length; ++i) {
			Type type = types[i];
			result[i] = createArg((Class<?>) type);
		}
		return result;
	}

	private Object createArg(Class<?> c) {
		if (c.equals(int.class)) {
			return Integer.valueOf(0);
		} else if (c.equals(long.class)) {
			return Long.valueOf(0L);
		} else if (c.equals(boolean.class)) {
			return Boolean.FALSE;
		} else if (c.equals(char.class)) {
			return Character.valueOf((char)0);
		} else if (c.equals(byte.class)) {
			return Byte.valueOf((byte)0);
		} else if (c.equals(short.class)) {
			return Short.valueOf((short)0);
		} else if (c.equals(float.class)) {
			return Float.valueOf(0.0f);
		} else if (c.equals(double.class)) {
			return Double.valueOf(0.0d);
		} else {
			return null;
		}
	}

	private boolean checkDestructiveMethod(final Method m) {
		return m.getReturnType().equals(Void.TYPE) && !Modifier.isStatic(m.getModifiers());
	}

	protected abstract T createModel();
}
