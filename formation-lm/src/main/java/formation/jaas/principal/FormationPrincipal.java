package formation.jaas.principal;


import java.security.Principal;

public class FormationPrincipal implements Principal, java.io.Serializable {

	private static final long serialVersionUID = 2566065251327054435L;
	/**
     * @serial
     */
    private String name;

    /**
     * Create a FormationPrincipal with a Sample username.
     *
     * <p>
     *
     * @param name the Sample username for this user.
     *
     * @exception NullPointerException if the <code>name</code>
     *                  is <code>null</code>.
     */
    public FormationPrincipal(String name) {
        if (name == null)
            throw new NullPointerException("illegal null input");

        this.name = name;
    }

    /**
     * Return the Sample username for this <code>FormationPrincipal</code>.
     *
     * <p>
     *
     * @return the Sample username for this <code>FormationPrincipal</code>
     */
    public String getName() {
        return name;
    }

    /**
     * Return a string representation of this <code>FormationPrincipal</code>.
     *
     * <p>
     *
     * @return a string representation of this <code>FormationPrincipal</code>.
     */
    public String toString() {
        return("FormationPrincipal:  " + name);
    }

    /**
     * Compares the specified Object with this <code>FormationPrincipal</code>
     * for equality.  Returns true if the given object is also a
     * <code>FormationPrincipal</code> and the two SamplePrincipals
     * have the same username.
     *
     * <p>
     *
     * @param o Object to be compared for equality with this
     *          <code>FormationPrincipal</code>.
     *
     * @return true if the specified Object is equal equal to this
     *          <code>FormationPrincipal</code>.
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof FormationPrincipal))
            return false;
        FormationPrincipal that = (FormationPrincipal)o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }

    /**
     * Return a hash code for this <code>FormationPrincipal</code>.
     *
     * <p>
     *
     * @return a hash code for this <code>FormationPrincipal</code>.
     */
    public int hashCode() {
        return name.hashCode();
    }
}
