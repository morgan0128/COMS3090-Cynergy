package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre, Tanya Ken
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Person {

    private String firstName;

    private String lastName;

    private String address;

    private String telephone;

    // New addition for experiment
    private List<String> pets = new ArrayList<>();
    public Person(String firstName, String lastName, String address, String telephone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.pets = new ArrayList<>();
    }

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + address + " "
               + telephone + " "
                + pets;
    }
}
