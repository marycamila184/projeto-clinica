package com.up.clinica.model.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.up.clinica.model.ConnectionFactory;
import com.up.clinica.model.TipoAnimal;

public class TipoAnimalDAO extends AbstractDAO<TipoAnimal, String> {

	@Override
	protected PreparedStatement criarStatementBuscar(Connection conexao, String id) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("SELECT ACRONIMO, NOME, DESCRICAO FROM TIPO_ANIMAL WHERE ACRONIMO = ?");
		statement.setString(1, id);
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementRemover(Connection conexao, String id) throws Exception {
		PreparedStatement statement= conexao.prepareStatement("DELETE FROM TIPO_ANIMAL WHERE ACRONIMO=?");
		statement.setString(1, id);
		return statement;
	}

	@Override
	protected void carregarChavesGeradasNoObjeto(ResultSet generatedKeys, TipoAnimal objeto) throws Exception {
		objeto.setAcronimo(generatedKeys.getString(1));		
	}

	@Override
	protected PreparedStatement criarStatementPersistir(Connection conexao, TipoAnimal objeto) throws Exception {
		PreparedStatement statement = conexao.prepareStatement(
				"INSERT INTO TIPO_ANIMAL (ACRONIMO,NOME,DESCRICAO) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
		statement.setString(1, objeto.getAcronimo());
		statement.setString(2, objeto.getNome());
		statement.setString(3, objeto.getDescricao());
		return statement;
	}

	@Override
	protected PreparedStatement criarStatementAtualizar(Connection conexao, TipoAnimal objeto) throws Exception {
		PreparedStatement statement = conexao
				.prepareStatement("UPDATE TIPO_ANIMAL SET NOME=?, DESCRICAO=? WHERE ACRONIMO=?");
		statement.setString(1, objeto.getNome());
		statement.setString(2, objeto.getDescricao());

		return statement;
	}

	@Override
	protected PreparedStatement criarStatementListar(Connection conexao) throws Exception {
		return conexao.prepareStatement("SELECT ACRONIMO, NOME, DESCRICAO FROM TIPO_ANIMAL;");
	}

	@Override
	protected TipoAnimal parseObjeto(ResultSet rs) throws Exception {
		TipoAnimal t = new TipoAnimal();
		t.setAcronimo(rs.getString(1));
		t.setNome(rs.getString(2));
		t.setDescricao(rs.getString(3));
		
		return t;
	}
	
	protected PreparedStatement criarStatementRemoveComRelacionamentos(Connection conexao, String id) throws Exception {
		PreparedStatement statement= conexao.prepareStatement("DELETE FROM ESPECIE WHERE TIPO_ANIMAL_ACRONIMO=?");
		statement.setString(1, id);
		return statement;
	}
	
	public void removerComRelacionamento(String acronimoTipoAnimal) throws Exception {
		Connection con = null;
		PreparedStatement statementRelacionamentos = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		Exception ultimaExcecao = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			
			statementRelacionamentos = this.criarStatementRemoveComRelacionamentos(con, acronimoTipoAnimal);
			statementRelacionamentos.executeUpdate();
			
			statement = this.criarStatementRemover(con, acronimoTipoAnimal);
			statement.executeUpdate();
			con.commit();
			return;
		} catch (Exception e) {
			ultimaExcecao = e;
			con.rollback();
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (SQLException e) {
				ultimaExcecao = e;
			}
			try {
				if (statement != null)
					statement.close();
				
				if (statementRelacionamentos != null)
					statementRelacionamentos.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
				ultimaExcecao = e;
			}
		}
		throw ultimaExcecao;
	}
}
