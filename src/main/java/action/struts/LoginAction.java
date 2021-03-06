/*
 * Copyright © 2017 Daniel Dias (daniel.dias.analistati@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package action.struts;

import action.form.bean.UsuarioForm;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelos.Usuario;
import modelos.UsuarioVip;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import persistencia.DAOFactory;
import persistencia.GenericDAO;

/**
 * @author daniel
 * github:Daniel-Dos
 * daniel.dias.analistati@gmail.com
 * twitter:@danieldiasjava
 */
public class LoginAction extends org.apache.struts.action.Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Usuario usuario = null;
		DAOFactory df = null;
		String msg = null, msg1 = null;
		GenericDAO<Usuario> daoUsuario = null;

		try {

			df = DAOFactory.getDaoFactory(DAOFactory.HIBERNATE);
			daoUsuario = (GenericDAO<Usuario>) df.getGenericoDAOUsuarioHibernate();

			usuario = new UsuarioVip();
			BeanUtils.copyProperties(usuario, (UsuarioForm) form);

			usuario = daoUsuario.consultarLoginSenha(usuario);

			String acao = request.getParameter("acao");

			if (usuario != null) {
				request.getSession().setAttribute("usuario", usuario);
				acao = "V";
			}

			if (acao.equals("V")) {
				// Verificação
				HttpSession session = request.getSession();
				if (session.getAttribute("usuario") != null) {
					msg = "Seja Bem Vindo  ";
					request.setAttribute("msg", msg);
				} else {
					request.getSession().invalidate();
					msg1 = "Usuario/Senha não existe ou não Cadastrado!!";
					request.setAttribute("msg1", msg1);

					return mapping.findForward("erroLogin");
				}
			}

		} catch (ClassNotFoundException e) {
			msg1 = "Erro de Driver";
			System.out.println(e.getMessage());
			request.setAttribute("msg1", msg1);
			return mapping.findForward("erroLogin");
		} catch (SQLException | ConnectException e) {
			msg1 = "Erro de SQL - Erro ao conectar no servidor 'localhost' porta '1527";
			System.out.println(e.getMessage());
			request.setAttribute("msg1", msg1);
			return mapping.findForward("erroLogin");
		} catch (Exception e) {
			msg1 = "Erro generico";
			System.out.println(e.getMessage());
			request.setAttribute("msg1", msg1);
			return mapping.findForward("erroLogin");
		}
		return mapping.findForward("logado");
	}
}