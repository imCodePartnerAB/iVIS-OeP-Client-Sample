package se.unlogic.hierarchy.core.utils.crud;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.core.utils.GenericCRUD;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;


public class ModularCRUD<BeanType extends Elementable, IDType, UserType extends User, CallbackType extends CRUDCallback<UserType>> extends GenericCRUD<BeanType, IDType, UserType, CallbackType> {

	protected BeanIDParser<IDType> idParser;
	
	protected List<RequestFilter> requestFilters;
	
	protected List<BeanFilter<? super BeanType>> beanFilters;
	
	protected List<AccessFilter<? super BeanType>> accessFilters;
	
	public ModularCRUD(BeanIDParser<IDType> idParser, CRUDDAO<BeanType, IDType> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);

		this.idParser = idParser;
	}

	public ModularCRUD(BeanIDParser<IDType> idParser, CRUDDAO<BeanType, IDType> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeElementPluralName, String typeLogName, String typeLogPluralName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeElementPluralName, typeLogName, typeLogPluralName, listMethodAlias, callback);

		this.idParser = idParser;
	}

	@Override
	public BeanType getRequestedBean(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, String getMode) throws SQLException, AccessDeniedException {

		IDType beanID = idParser.getBeanID(uriParser, req, getMode);
		
		if(beanID != null){
			
			BeanType bean = getBean(beanID, getMode, req);
			
			if(bean != null && beanFilters != null){
				
				for(BeanFilter<? super BeanType> beanFilter : beanFilters){
					
					beanFilter.beanLoaded(bean, req, uriParser, user);
				}
			}
			
			return bean;
		}
		
		return null;
	}

	public BeanType getBean(IDType beanID, String getMode, HttpServletRequest req) throws SQLException, AccessDeniedException {

		TransactionHandler transactionHandler = getTransactionHandler(req);
		
		if(transactionHandler != null){
			
			return crudDAO.get(beanID, transactionHandler);
			
		}else{
			
			return crudDAO.get(beanID);
		}	
	}	
	
	@Override
	protected void addBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.addBean(bean, req, uriParser, user);
			}
		}
		
		addFilteredBean(bean, req, user, uriParser);
	}

	protected void addFilteredBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		TransactionHandler transactionHandler = getTransactionHandler(req);
		
		if(transactionHandler != null){
			
			crudDAO.add(bean, transactionHandler);
			
			transactionHandler.commit();
			
		}else{
			
			crudDAO.add(bean);
		}
	}

	@Override
	protected void updateBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.updateBean(bean, req, uriParser, user);
			}
		}
		
		updateFilteredBean(bean, req, user, uriParser);
	}

	protected void updateFilteredBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		TransactionHandler transactionHandler = getTransactionHandler(req);
		
		if(transactionHandler != null){
			
			crudDAO.update(bean, transactionHandler);
			
			transactionHandler.commit();
			
		}else{
			
			crudDAO.update(bean);
		}
	}

	@Override
	protected void deleteBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.deleteBean(bean, req, uriParser, user);
			}
		}
		
		deleteFilteredBean(bean, req, user, uriParser);
	}

	protected void deleteFilteredBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		TransactionHandler transactionHandler = getTransactionHandler(req);
		
		if(transactionHandler != null){
			
			crudDAO.delete(bean, transactionHandler);
			
			transactionHandler.commit();
			
		}else{
			
			crudDAO.delete(bean);
		}	
	}

	@Override
	protected ForegroundModuleResponse beanAdded(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.beanAdded(bean, req, uriParser, user);
			}
		}		
		
		return filteredBeanAdded(bean, req, res, user, uriParser);
	}

	protected ForegroundModuleResponse filteredBeanAdded(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		return super.beanAdded(bean, req, res, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.beanUpdated(bean, req, uriParser, user);
			}
		}	
		
		return filteredBeanUpdated(bean, req, res, user, uriParser);
	}

	protected ForegroundModuleResponse filteredBeanUpdated(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		return super.beanUpdated(bean, req, res, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanDeleted(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		if(beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.beanDeleted(bean, req, uriParser, user);
			}
		}	
		
		return filteredBeanDeleted(bean, req, res, user, uriParser);
	}

	protected ForegroundModuleResponse filteredBeanDeleted(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		return super.beanDeleted(bean, req, res, user, uriParser);
	}

	@Override
	protected List<BeanType> getAllBeans(UserType user, HttpServletRequest req, URIParser uriParser) throws SQLException {

		List<BeanType> beans = super.getAllBeans(user);
		
		if(beans != null && beanFilters != null){
			
			for(BeanFilter<? super BeanType> beanFilter : beanFilters){
				
				beanFilter.beansLoaded(beans, req, uriParser, user);
			}
		}
		
		return beans;
	}

	@Override
	protected HttpServletRequest parseRequest(HttpServletRequest req, UserType user) throws ValidationException, Exception {

		if(requestFilters != null){
			
			for(RequestFilter requestFilter : requestFilters){
				
				req = requestFilter.parseRequest(req, user);
			}
			
		}
		
		return req;
	}

	@Override
	protected void checkAddAccess(UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(accessFilters != null){
			
			for(AccessFilter<? super BeanType> accessFilter : accessFilters){
				
				accessFilter.checkAddAccess(user, req, uriParser);
			}
		}	
	}

	@Override
	protected void checkUpdateAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(accessFilters != null){
			
			for(AccessFilter<? super BeanType> accessFilter : accessFilters){
				
				accessFilter.checkUpdateAccess(bean, user, req, uriParser);
			}
		}
	}	
	
	@Override
	protected void checkDeleteAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(accessFilters != null){
			
			for(AccessFilter<? super BeanType> accessFilter : accessFilters){
				
				accessFilter.checkDeleteAccess(bean, user, req, uriParser);
			}
		}
	}

	@Override
	protected void checkShowAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(accessFilters != null){
			
			for(AccessFilter<? super BeanType> accessFilter : accessFilters){
				
				accessFilter.checkShowAccess(bean, user, req, uriParser);
			}
		}
	}

	@Override
	protected void checkListAccess(UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(accessFilters != null){
			
			for(AccessFilter<? super BeanType> accessFilter : accessFilters){
				
				accessFilter.checkListAccess(user, req, uriParser);
			}
		}	
	}	
	
	@Override
	protected void releaseRequest(HttpServletRequest req, UserType user) {

		if(requestFilters != null){
			
			for(RequestFilter requestFilter : requestFilters){
				
				requestFilter.releaseRequest(req, user);
			}
		}
	}	
	
	public synchronized void addRequestFilter(RequestFilter requestFilter){
		
		if(this.requestFilters == null){
			
			this.requestFilters = new CopyOnWriteArrayList<RequestFilter>();
		}
		
		this.requestFilters.add(requestFilter);
	}
	
	public synchronized boolean removeRequestFilter(RequestFilter requestFilter){
		
		if(this.requestFilters != null){
			
			return requestFilters.remove(requestFilter);
		}
		
		return false;
	}
	
	public synchronized void addBeanFilter(BeanFilter<? super BeanType> beanFilter){
		
		if(this.beanFilters == null){
			
			this.beanFilters = new CopyOnWriteArrayList<BeanFilter<? super BeanType>>();
		}
		
		this.beanFilters.add(beanFilter);
	}
	
	public synchronized boolean removeBeanFilter(BeanFilter<? super BeanType> beanFilter){
		
		if(this.beanFilters != null){
			
			return beanFilters.remove(beanFilter);
		}
		
		return false;
	}
	
	public synchronized void addAccessFilter(AccessFilter<? super BeanType> accessFilter){
		
		if(this.accessFilters == null){
			
			this.accessFilters = new CopyOnWriteArrayList<AccessFilter<? super BeanType>>();
		}
		
		this.accessFilters.add(accessFilter);
	}
	
	public synchronized boolean removeAccessFilter(AccessFilter<? super BeanType> accessFilter){
		
		if(this.accessFilters != null){
			
			return accessFilters.remove(accessFilter);
		}
		
		return false;
	}
	
	protected TransactionHandler getTransactionHandler(HttpServletRequest req) {

		return (TransactionHandler) req.getAttribute(TransactionRequestFilter.TRANSACTION_HANDLER_REQUEST_ATTRIBUTE);
	}	
}
