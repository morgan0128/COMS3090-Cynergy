package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre, Tanya Ken
 */

@RestController
public class PeopleController {
    HashMap<String, Person> peopleList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL


    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public  HashMap<String,Person> getAllPersons() {
        return peopleList;
    }


    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public  String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        return "Added "+ person.getFirstName() + " to the system.";
    }

    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/people/contains")
    public List<Person> getPersonByParam(@RequestParam("name") String name) {
        List<Person> res = new ArrayList<>();
        for (Person p : peopleList.values()) {
            if (p.getFirstName().contains(name) || p.getLastName().contains(name))
                res.add(p);
        }
        return res;
    }

    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public Person updatePerson(@PathVariable String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }

    // UPDATE using RequestParam and ResponseBody with alternate style of declaring params
    @PutMapping(
            value="/people",
            params = { "firstName" }
    )
    public Person updatePerson2(@RequestParam("firstName") String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }

    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }

    // MODIFICATIONS FOR EXP2
    // Experimenting with CRUDL by using the respective methods (POST, GET, PUT, DELETE, GET)
    // Add a new pet for a person
    @PostMapping("/people/{firstName}/pets")
    public String addPet(@PathVariable String firstName, @RequestBody String pet) {
        Person p = peopleList.get(firstName);
        if (p == null) {
            return "Person not found!";
        }
        p.getPets().add(pet);
        return "Added pet " + pet + " to " + firstName;
    }

    // MODIFICATIONS FOR EXP3: ADDED OPTIONS TO UPDATE, DELETE, AND VIEW PETS SPECIFICALLY
    // Update pets
    @PutMapping("/people/{firstName}/pets")
    public Person updatePets(@PathVariable String firstName, @RequestBody List<String> pets) {
        Person p = peopleList.get(firstName);
        if (p != null) {
            p.setPets(pets);
        }
        return p;
    }

    // Delete a pet
    @DeleteMapping("/people/{firstName}/pets/{petName}")
    public String deletePet(@PathVariable String firstName, @PathVariable String petName) {
        Person p = peopleList.get(firstName);
        if (p != null && p.getPets().remove(petName)) {
            return "Removed pet " + petName + " from " + firstName;
        }
        return "Are you sure " + firstName + " owns this pet?";
    }

    // View a person's pet
    @GetMapping("/people/{firstName}/pets")
    public List<String> getPets(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return (p != null) ? p.getPets() : new ArrayList<>();
    }
}

