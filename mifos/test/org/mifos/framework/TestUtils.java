package org.mifos.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestUtils {
	
	public static final int ADMIN_ROLE = 1;

	public static UserContext makeUser() {
		return makeUser(ADMIN_ROLE);
	}

	/**
	 * Also see {@link TestObjectFactory#getUserContext()} which should be
	 * slower (it involves several database accesses).
	 */
	public static UserContext makeUser(int role) {
		UserContext user = new UserContext();
		user.setId(PersonnelConstants.SYSTEM_USER);
		user.setLocaleId(TestObjectFactory.TEST_LOCALE);
		Set<Short> set = new HashSet<Short>();
		set.add((short) role);
		user.setRoles(set);
		user.setLevel(PersonnelLevel.NON_LOAN_OFFICER);
		user.setName("mifos");
		user.setPreferredLocale(new Locale("en", "US"));
		user.setBranchId((short)1);
		user.setBranchGlobalNum("0001");
		return user;
	}

	public static void assertWellFormedFragment(String xml) 
	throws DocumentException {
		assertWellFormedDocument("<root>" + xml + "</root>");
	}

	public static void assertWellFormedDocument(String xmlDocument) 
	throws DocumentException {
		SAXReader reader = new SAXReader();
		reader.read(new StringReader(xmlDocument));
	}

    /*
     * Here is our equals/hashCode testing framework.  Is there really
     * not just one to download?  This wheel gets reinvented so often.
     * The one in junitx.extensions.EqualsHashCodeTestCase is seriously broken -
     * it often gets confused about which equals method it is testing
     * (e.g. the one from Object or the one under test) and similar
     * problems.
     */
    
	/**
	 * Verify equals contract.  A single call to this method will generally
	 * suffice to test equals and hashCode.  Just make sure to pass in
	 * enough examples of equal and not-equal objects to cover each of
	 * the cases in your equals implementation.  Generally there should be
	 * an instance of a subclass somewhere in the data you pass.  The null case
	 * is always checked and should not be passed in either the 
	 * equalArray or noEqualArray.
	 * 
	 * @param equalArray - an array of class T containing at least 2 elements
	 * which are all equal to one another (eg. new Foo[] {new Foo(5), new Foo(5)})
	 * @param notEqualArray - an array of class T containing at least 1 element all of which
	 * are not equal to the equalArray[0] parameter (eg. new Foo[] {Foo(4)} )
	 */
	public static <T> void verifyBasicEqualsContract(
			T[] equalArray, T[] notEqualArray) {
		if (equalArray.length < 2) {
			Assert.fail("equalArray requires at least 2 elements (but only had "
							+ equalArray.length + ")");
		}
		if (notEqualArray.length < 1) {
			Assert.fail("notEqualArray requires at least 1 element");
		}
		T equalObject = equalArray[0];

		// verify equals contract for equal objects of the same class
		assertAllEqual(equalArray);

		// verify inequality of an objects of the same class
		for (T notEqual : notEqualArray) {
			assertIsNotEqual(equalObject, notEqual);
		}

		// verify inequality of an unrelated class
		assertIsNotEqual(equalObject, new Object());
	}
		
    public static void assertAllEqual(Object[] objects) {
        /**
         * The point of checking each pair is to make sure that equals is
         * transitive per the contract of {@link Object#equals(java.lang.Object)}.
         */
        for (int i = 0; i < objects.length; i++) {
        	Assert.assertNotNull(
            	"You don't need to pass null; null is checked for you", 
            	objects[i]);
            Assert.assertFalse(objects[i].equals(null));
            for (int j = 0; j < objects.length; j++) {
                assertIsEqual(objects[i], objects[j]);
            }
        }
    }

    /**
     * The reason this method should only be called from 
     * {@link #assertAllEqual(Object[])} is that the 
     * latter checks for reflexive and null.
     */
    private static void assertIsEqual(Object one, Object two) {
    	Assert.assertTrue(one.equals(two));
    	Assert.assertTrue(two.equals(one));
    	Assert.assertEquals(one.hashCode(), two.hashCode());
    }

    public static void assertIsNotEqual(Object one, Object two) {
        assertReflexiveAndNull(one);
        assertReflexiveAndNull(two);
        Assert.assertFalse(one.equals(two));
        Assert.assertFalse(two.equals(one));
        
        /* The hashCodes may or may not be equal, but they shouldn't
           throw an exception. */
        one.hashCode();
        two.hashCode();
    }

    public static void assertReflexiveAndNull(Object object) {
    	Assert.assertNotNull(
    		"You don't need to pass null; null is checked for you", object);
    	Assert.assertTrue(object.equals(object));
    	Assert.assertFalse(object.equals(null));
    }
    /* end equals testing methods */
    
	public static void assertCanSerialize(Object object) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = 
			new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		Assert.assertTrue(byteArrayOutputStream.toByteArray().length > 0);
	}

	public static void showMemory() {
		System.out.println("free: " + 
				Runtime.getRuntime().freeMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println("max: " + 
				Runtime.getRuntime().maxMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println("total: " + 
				Runtime.getRuntime().totalMemory()/ 1000000.0 +
				" MB"
				);
		System.out.println();
	}

}
