package br.com.on3solutions.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.on3solutions.model.Doc;
import br.com.on3solutions.repository.DocRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DocController {

	@Autowired
	DocRepository docRepository;
	/*
	 * @GetMapping("/tutorials") public ResponseEntity<List<Tutorial>>
	 * getAllTutorials(@RequestParam(required = false) String title) { try {
	 * List<Tutorial> tutorials = new ArrayList<Tutorial>();
	 * 
	 * if (title == null) tutorialRepository.findAll().forEach(tutorials::add); else
	 * tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
	 * 
	 * if (tutorials.isEmpty()) { return new
	 * ResponseEntity<>(HttpStatus.NO_CONTENT); }
	 * 
	 * return new ResponseEntity<>(tutorials, HttpStatus.OK); } catch (Exception e)
	 * { return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); } }
	 */

	@GetMapping("/doc/{frase}")
	public ResponseEntity<List<Doc>> getTutorialById(@PathVariable("frase") String frase) {

		TextCriteria search = TextCriteria.forDefaultLanguage().matchingPhrase(frase);
		List<Doc> resultado = docRepository.findAllBy(search);

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}
	
	

	@PostMapping("/doc")
	public ResponseEntity<Doc> createTutorial(@RequestBody @Valid Doc doc) throws FileNotFoundException, IOException {
		/*
		 * try { Tutorial _tutorial = tutorialRepository.save(new
		 * Tutorial(tutorial.getTitle(), tutorial.getDescription(), false)); return new
		 * ResponseEntity<>(_tutorial, HttpStatus.CREATED); } catch (Exception e) {
		 * return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); }
		 */

		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;

		byte[] decoded = java.util.Base64.getDecoder().decode(doc.getArquivo());

		String tmpdir = System.getProperty("java.io.tmpdir");
		File arquivoPDF = new File(tmpdir + "/" + "teste.pdf");

		FileOutputStream pdf = new FileOutputStream(arquivoPDF);
		pdf.write(decoded);
		pdf.flush();
		pdf.close();

		try {
			PDFParser parser = new PDFParser(new org.apache.pdfbox.io.RandomAccessFile(arquivoPDF, "r"));
			parser.parse();
			cosDoc = parser.getDocument();

			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);

			doc.setArquivo(pdfStripper.getText(pdDoc));

		} finally {
			try {
				if (cosDoc != null) {
					cosDoc.close();
				}
				if (pdDoc != null) {
					pdDoc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new ResponseEntity<>(docRepository.save(doc), HttpStatus.CREATED);

	}

	/*
	 * @PutMapping("/tutorials/{id}") public ResponseEntity<Tutorial>
	 * updateTutorial(@PathVariable("id") String id, @RequestBody Tutorial tutorial)
	 * { Optional<Tutorial> tutorialData = tutorialRepository.findById(id);
	 * 
	 * if (tutorialData.isPresent()) { Tutorial _tutorial = tutorialData.get();
	 * _tutorial.setTitle(tutorial.getTitle());
	 * _tutorial.setDescription(tutorial.getDescription());
	 * _tutorial.setPublished(tutorial.isPublished()); return new
	 * ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK); } else {
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND); } }
	 */
	/*
	 * @DeleteMapping("/tutorials/{id}") public ResponseEntity<HttpStatus>
	 * deleteTutorial(@PathVariable("id") String id) { try {
	 * tutorialRepository.deleteById(id); return new
	 * ResponseEntity<>(HttpStatus.NO_CONTENT); } catch (Exception e) { return new
	 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } }
	 */
	/*
	 * @DeleteMapping("/tutorials") public ResponseEntity<HttpStatus>
	 * deleteAllTutorials() { try { tutorialRepository.deleteAll(); return new
	 * ResponseEntity<>(HttpStatus.NO_CONTENT); } catch (Exception e) { return new
	 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } }
	 * 
	 * @GetMapping("/tutorials/published") public ResponseEntity<List<Tutorial>>
	 * findByPublished() { try { List<Tutorial> tutorials =
	 * tutorialRepository.findByPublished(true);
	 * 
	 * if (tutorials.isEmpty()) { return new
	 * ResponseEntity<>(HttpStatus.NO_CONTENT); } return new
	 * ResponseEntity<>(tutorials, HttpStatus.OK); } catch (Exception e) { return
	 * new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); } }
	 */
}
