/**
 * 
 */
package br.com.on3solutions.model;


import org.springframework.data.annotation.Id;

public class Document {

	@Id
	public Long id;

	public Long idLayout;
	public Long idTematica;
	public Long version;
	public String ambiente;
	public String bucket;
	public String firstName;
	public String lastName;
	public String arquivo;

	

}