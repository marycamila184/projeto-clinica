package com.up.clinica.servico;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.up.clinica.model.dal.AbstractDAO;
import com.up.clinica.servico.converter.JsonConverter;

public abstract class AbstractService<C extends JsonConverter<T>, D extends AbstractDAO<T, U>, T, U>
		implements IService<T, U> {

	private D dao;
	private C converter;

	public AbstractService(D dao, C converter) {
		this.dao = dao;
		this.converter = converter;
	}

	public final void iniciaServico(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String resultadoJson = "";

		// Pegando o serviço
		String servico = request.getParameter("servico");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		if (servico.equals("listar")) {
			// Listando os animais
			resultadoJson = listar();

		} else if (servico.equals("buscar")) {
			// Buscando um animal
			resultadoJson = buscar(request.getParameter("id"));			

		} else if (servico.equals("cadastrar")) {
			T object = parserStringToObject(request.getReader());

			// Validando a model
			if (!validatorModel(object)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// Cadastrando o animal
				cadastrar(object);
			}

		} else if (servico.equals("remover")) {
			// Removendo o animal
			remover(request.getParameter("id"));

		} else if (servico.equals("alterar")) {
			T object = parserStringToObject(request.getReader());

			// Validando a model
			if (!validatorModel(object)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				// Alterando o animal
				alterar(object);
			}

		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

		response.getWriter().write(resultadoJson);
	}

	@Override
	public String listar() throws Exception {
		return converter.convertToJsonString(dao.listar());
	}

	@Override
	public void alterar(T objeto) throws Exception {
		dao.atualizar(objeto);
	}

	@Override
	public String buscar(String id) throws Exception {
		return converter.convertToJsonString(dao.buscar(converterId(id)));
	}

	@Override
	public void cadastrar(T objeto) throws Exception {
		dao.persistir(objeto);
	}

	@Override
	public void remover(String id) throws Exception {		
		dao.remover(converterId(id));
	}

	// Parser de String para Objeto T
	private T parserStringToObject(BufferedReader reader) throws IOException {
		// Fazendo o parser do json em objeto
		StringBuffer jb = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null)
			jb.append(line);
		return converter.convertToObject(jb.toString());
	}

	// Valida model para cadastro e alteração
	public abstract boolean validatorModel(T objeto);

	// Converte e retorna o parametro id na forma U
	public abstract U converterId(String id);
}
