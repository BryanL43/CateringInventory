package org.fakeskymeal.dao;

import java.lang.reflect.Field;

/**
 * BaseDto
 *
 * Base class for HR DTOs to contain some common methods for DTOs.
 *
 * Modifications:
 *
 * 		04/20/2024 - jhui - Created
 */

public class BaseDto {

    public BaseDto() {
        super();
    }

    /**
     * toString
     *
     * returns the current DTO as a formatted String.  Does not support embedded DTO's (left as exercise)
     *
     * 	className
     * 		field1Name: field1Value
     * 		field2Name: field2Value
     * 		...
     *
     * @return String
     */
    public String toString() {
        StringBuilder output = new StringBuilder("\n");

        output.append(this.getClass().getName());
        output.append("\n");

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            output.append("\n\t");
            output.append(field.getName());
            output.append(": ");
            try {
                output.append(field.get(this));
            } catch (Exception e) {
                System.out.println("Exception in toString: " + e.getMessage());

                throw new RuntimeException(e);
            }
            output.append("\n");
        }
        output.append("\n");

        return output.toString();
    }


    /**
     * toJson
     *
     * returns the current DTO as JSON.  Does not support embedded DTO's (left as exercise)
     *
     * 	{
     * 		<className>: {
     * 			<field1Name>: value,
     * 			<field2Name>: value,
     * 			...
     * 			<fieldnName: value
     * 		}
     * 	}
     *
     * @return String in Json format
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("\n{\n");

        json.append("\t\"");
        json.append(this.getClass().getName());
        json.append("\": {\n");

        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            json.append("\t\t\"");
            json.append(fields[i].getName());
            json.append("\": \"");
            try {
                json.append(fields[i].get(this));
            }
            catch (Exception e) {
                System.out.println("Exception in toJson: " + e.getMessage());

                throw new RuntimeException(e);
            }
            json.append("\"");
            if (i < fields.length - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("\t}\n");
        json.append("}");

        return json.toString();
    }
}
