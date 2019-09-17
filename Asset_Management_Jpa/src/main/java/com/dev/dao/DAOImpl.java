package com.dev.dao;

import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.dev.beans.Asset;
import com.dev.beans.AssetAllocation;
import com.dev.beans.AssetStatus;
import com.dev.beans.Employee;
import com.dev.beans.UserMaster;
import com.dev.exceptions.AddAssetException;
import com.dev.exceptions.AddEmployeeException;
import com.dev.exceptions.AssetAllocationException;
import com.dev.exceptions.InvalidDepartmentException;
import com.dev.exceptions.LoginException;
import com.dev.exceptions.RaiseAllocationException;
import com.dev.exceptions.RemoveAssetException;
import com.dev.exceptions.StatusException;
import com.dev.exceptions.UpdateAssetException;

public class DAOImpl implements DAO {
	EntityManagerFactory entityManagerFactory = null;

	@Override
	public UserMaster login(Integer userid, String password) {
		UserMaster um = new UserMaster();
		entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		String jpql = "select usertype from UserMaster where userid=:uid and userpassword=:upwd";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("uid", userid);
		query.setParameter("upwd", password);
		String user = (String) query.getSingleResult();
		um.setUsertype(user);
		entityManager.close();
		return um;
	}

	@Override
	public AssetAllocation raiseAllocation(AssetAllocation assetallocation) {
		try {
			AssetStatus assetStatus = new AssetStatus();
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			AssetAllocation assetAllocation1 = entityManager.find(AssetAllocation.class,
					assetallocation.getAllocationid());
			if (assetAllocation1 == null) {
				EntityTransaction entityTransaction = entityManager.getTransaction();
				entityTransaction.begin();
				entityManager.persist(assetallocation);
				assetStatus.setAllocationid(assetallocation.getAllocationid());
				assetStatus.setStatus("null");
				entityManager.persist(assetStatus);
				entityTransaction.commit();
				entityManager.close();
				return assetallocation;
			} else {
				throw new RaiseAllocationException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public String viewStatus(Integer allocationid) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			String jpql = "select status from AssetStatus where allocationid=:aid";
			Query query = entityManager.createQuery(jpql);
			query.setParameter("allocid", allocationid);
			String status = (String) query.getSingleResult();
			if (status.equals("null")) {
				return ("Status not updated till now");
			} else {
				return ("Status:" + status);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		throw new StatusException();
//		return "Enter the valid allocation Id";
	}

	@Override
	public Employee addEmployee(Employee employee) {

		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			Employee employee1 = entityManager.find(Employee.class, employee.getEmpno());
			if (employee1 == null) {
				String jpql = "select deptid from Department ";
				Query query = entityManager.createQuery(jpql);
				List<Integer> list = query.getResultList();
				for (Integer emp : list) {
					if (employee.getDeptid() == emp) {
						EntityTransaction entityTransaction = entityManager.getTransaction();
						entityTransaction.begin();
						entityManager.persist(employee);
						entityTransaction.commit();
						return employee;
					}
				}
				// throw new AddEmployeeException();
			} else {
				throw new AddEmployeeException();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Asset addAsset(Asset asset) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			Asset asset1 = entityManager.find(Asset.class, asset.getAssetid());
			if (asset1 == null) {
				EntityTransaction entityTransaction = entityManager.getTransaction();
				entityTransaction.begin();
				entityManager.persist(asset);
				entityTransaction.commit();
				entityManager.close();
				return asset;
			}

			else {
				// throw new AddAssetException("asset is already present");
				throw new AddAssetException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Asset removeAsset(Integer aid) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			String jpql = "Delete from Asset where assetid=:assid";
			Asset asset = entityManager.find(Asset.class, aid);
			Query query = entityManager.createQuery(jpql);
			query.setParameter("assid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				entityTransaction.commit();
				return asset;
			} else {
				throw new RemoveAssetException();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Asset updateAssetName(Integer aid, String assetname) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			Asset asset = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql = "UPDATE Asset SET assetname=:asname WHERE assetid=:aid";

			Query query = entityManager.createQuery(jpql);
			query.setParameter("asname", assetname);
			query.setParameter("aid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				asset.setAssetname(assetname);
				entityTransaction.commit();
				return asset;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}

	public Asset updateAssetDes(Integer aid, String assetdes) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			Asset asset1 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql = "UPDATE Asset SET assetdes=:asdes WHERE assetid=:aid";
			Query query = entityManager.createQuery(jpql);
			query.setParameter("asdes", assetdes);
			query.setParameter("aid", aid);
			Integer i = query.executeUpdate();
			if (i > 0) {
				asset1.setAssetdes(assetdes);
				entityTransaction.commit();
				return asset1;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Asset updateAssetQuantity(Integer aid, Integer assetquan) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			Asset asset2 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql2 = "UPDATE Asset SET quantity=:aquan WHERE assetid=:aid";

			Query query2 = entityManager.createQuery(jpql2);
			query2.setParameter("aquan", assetquan);
			query2.setParameter("aid", aid);
			Integer i2 = query2.executeUpdate();
			if (i2 > 0) {
				asset2.setQuantity(assetquan);
				entityTransaction.commit();
				return asset2;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Asset updateAssetStatus(Integer aid, String assetstatus) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			Asset asset3 = entityManager.find(Asset.class, aid);
			entityTransaction.begin();
			String jpql3 = "UPDATE Asset SET status=:assetstatus WHERE assetid=:aid";

			Query query3 = entityManager.createQuery(jpql3);
			query3.setParameter("assetstatus", assetstatus);
			query3.setParameter("aid", aid);
			Integer i3 = query3.executeUpdate();
			if (i3 > 0) {
				asset3.setStatus(assetstatus);
				entityTransaction.commit();
				return asset3;
			} else {
				throw new UpdateAssetException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Asset> getAllAsset() {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			String jpql = "from Asset";
			Query query = entityManager.createQuery(jpql);
			List<Asset> list = query.getResultList();
			for (Asset stud : list) {
				System.out.println("ASSet Id:" + stud.getAssetid());
				System.out.println("ASSET name:" + stud.getAssetname());
				System.out.println("ASSET Description:" + stud.getAssetdes());
				System.out.println("ASSET Quantity:" + stud.getQuantity());
				System.out.println("ASSET Status:" + stud.getStatus());
				System.out.println("*********************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<AssetAllocation> getAllAssetAllocation() {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			String jpql = "from AssetAllocation";
			Query query = entityManager.createQuery(jpql);
			List<AssetAllocation> list = query.getResultList();
			if (!list.isEmpty()) {
				for (AssetAllocation stud : list) {
					System.out.println("Allocation Id:" + stud.getAllocationid());
					System.out.println("ASSET id:" + stud.getAssetid());
					System.out.println("Employee Number:" + stud.getEmpno());
					System.out.println("Allocation Date:" + stud.getAllocationdate());
					System.out.println("Release Date:" + stud.getReleasedate());
					System.out.println("*********************");
					entityManager.close();
				}
			} else {
				throw new AssetAllocationException();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;

	}

	@Override
	public Boolean setStatus(Integer allocationid, String status) {
		try {
			entityManagerFactory = Persistence.createEntityManagerFactory("asset_management_JPA");
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			String jpql = "UPDATE AssetStatus SET status=:asname WHERE allocationid=:aid";
			Query query = entityManager.createQuery(jpql);
			query.setParameter("asname", status);
			query.setParameter("aid", allocationid);
			Integer i = query.executeUpdate();
			entityTransaction.commit();
			if (i > 0) {
				entityTransaction.commit();
				return true;
			} else {
				throw new StatusException();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

}