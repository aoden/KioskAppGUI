package com.tdt.kioskapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author aoden
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Key {

    @Id
    protected String key;
}
