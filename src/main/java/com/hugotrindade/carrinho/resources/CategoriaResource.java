package com.hugotrindade.carrinho.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hugotrindade.carrinho.domain.Categoria;
import com.hugotrindade.carrinho.dto.CategoriaDTO;
import com.hugotrindade.carrinho.services.CategoriaService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaService service;

	@ApiOperation(value="Busca por id")
	@GetMapping(value="/{id}")
	public ResponseEntity<Categoria> find(@PathVariable Integer id) {
		return ResponseEntity.ok().body(service.find(id));
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@ApiOperation(value="Insere categoria")
	@PostMapping
	public ResponseEntity<Void> save(@Valid @RequestBody CategoriaDTO dto) {
		Categoria categoria = service.fromDTO(dto);
		categoria  = service.insert(categoria);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(categoria.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@ApiOperation(value="Atualiza categoria")
	@PutMapping(value="/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody CategoriaDTO dto, @PathVariable Integer id) {
		
		Categoria categoria = service.fromDTO(dto);
		categoria.setId(id);
		service.update(categoria);
		return ResponseEntity.noContent().build();
		
	}
	
	@ApiOperation(value="Retorna todas categorias")
	@GetMapping
	public ResponseEntity<List<CategoriaDTO>> findAll() {
		List<Categoria> categorias = service.findAll();
		List<CategoriaDTO> categoriasDto = categorias.stream().map(c -> new CategoriaDTO(c)).collect(Collectors.toList());
		return ResponseEntity.ok().body(categoriasDto);
	}
	
	@ApiOperation(value="Retorna todas categorias com paginação")
	@GetMapping(value="/page")
	public ResponseEntity<Page<CategoriaDTO>> findPage(
			@RequestParam(value="page", defaultValue="0")Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="20")Integer linesPerPage, 
			@RequestParam(value="orderBy", defaultValue="nome")String orderBy, 
			@RequestParam(value="direction", defaultValue="ASC")String direction) {
		
		Page<Categoria> categorias = service.findPage(page, linesPerPage, orderBy, direction);
		Page<CategoriaDTO> categoriasDto = categorias.map(c -> new CategoriaDTO(c));
		return ResponseEntity.ok().body(categoriasDto);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')")
	@ApiOperation(value="Remove categoria")
	@DeleteMapping(value="/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
		
	}
}
