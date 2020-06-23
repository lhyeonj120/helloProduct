package kr.ac.hansung.cse.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repo.ProductRepository;


@RestController // @Controller + @ResponseBody
@RequestMapping("/api/v1")
public class ProductController {

	@Autowired
	ProductRepository repository;
	
	// Create new product
	@PostMapping("/products")
	public ResponseEntity<Product> createProduct(@RequestBody Product product){
		try {
			Product _product = repository.save(new Product(product.getName(), product.getCategory(), product.getPrice(), product.getManufacturer(), product.getUnitInStock(), product.getDescription()));
			
			return new ResponseEntity<>(_product, HttpStatus.CREATED);
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);	// 417
		}
	}
	
	// Get full list of products
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts(){
		List<Product> products = new ArrayList<>();
		
		try {
			repository.findAll().forEach(products::add);	// DB를 조회해서 products에 하나씩 집어 넣음
			
			if(products.isEmpty())
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
			return new ResponseEntity<>(products, HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);	// 500
		}
	}
	
	// Get details of product with id
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable("id") int id){
		Optional<Product> productData = repository.findById(id);	// productData가 있을 수도 있고 없을 수도 있으므로 Optional

		if(productData.isPresent())
			return new ResponseEntity<>(productData.get(), HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);	// 404
	}
	
	// Fetch all products of a category
	@GetMapping("/products/category/{category}")
	public ResponseEntity<List<Product>> getAllProductsOfCategory(@PathVariable("category") String category){
		List<Product> products = new ArrayList<>();
		
		try {
			repository.findByCategory(category).forEach(products::add);
			
			if(products.isEmpty())
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
			return new ResponseEntity<>(products, HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	// Modify values of product with id
	@PutMapping("/products/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable("id") int id, @RequestBody Product product){
		Optional<Product> productData = repository.findById(id);
		
		if(productData.isPresent()) {
			Product _product = productData.get();
			_product.setName(product.getName());
			_product.setCategory(product.getCategory());
			_product.setPrice(product.getPrice());
			_product.setManufacturer(product.getManufacturer());
			_product.setUnitInStock(product.getUnitInStock());
			_product.setDescription(product.getDescription());
			
			return new ResponseEntity<>(repository.save(_product), HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	// Delete product with id
	@DeleteMapping("/products/{id}")
	public ResponseEntity<HttpStatus> deleteProductById(@PathVariable("id") int id){
		try {
			repository.deleteById(id);
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}catch(Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
