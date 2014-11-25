/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.web;

import org.apache.log4j.Logger;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.directory.fortress.core.PwPolicyMgr;
import org.apache.directory.fortress.core.rbac.PwPolicy;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.SecurityException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn McKinney
 * @version $Rev$
 */
public class PwPolicyListModel extends Model<SerializableList<PwPolicy>>
{
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    @SpringBean
    private PwPolicyMgr pwPolicyMgr;
    private static final Logger LOG = Logger.getLogger(PwPolicyListModel.class.getName());
    private transient PwPolicy policy;
    private transient SerializableList<PwPolicy> policies = null;

    /**
     * Default constructor
     */
    public PwPolicyListModel( Session session )
    {
        Injector.get().inject(this);
        // TODO: enable this after search permission added:
        //this.pwPolicyMgr.setAdmin( session );
    }

    
    /**
     * User contains the search arguments.
     *
     * @param policy
     */
    public PwPolicyListModel( PwPolicy policy, Session session )
    {
        Injector.get().inject( this );
        this.policy = policy;
        // TODO: enable this after search permission added:
        //this.pwPolicyMgr.setAdmin( session );
    }
    

    /**
     * This data is bound for RoleListPanel
     *
     * @return T extends List<Role> roles data will be bound to panel data view component.
     */
    @Override
    public SerializableList<PwPolicy> getObject()
    {
        if ( policies != null )
        {
            LOG.debug( ".getObject count: " + policy != null ? policies.size() : "null" );
            return policies;
        }
        
        if ( policy == null )
        {
            LOG.debug( ".getObject null" );
            policies = new SerializableList<PwPolicy>( new ArrayList<PwPolicy>() );
        }
        else
        {
            LOG.debug( ".getObject policyNm: " + policy != null ? policy.getName() : "null" );
            policies = new SerializableList<PwPolicy>( getList( policy ) );
        }
        
        return policies;
    }

    
    @Override
    public void setObject( SerializableList<PwPolicy> object )
    {
        LOG.debug( ".setObject count: " + object != null ? ((List<PwPolicy>)object).size() : "null" );
        policies = object;
    }
    

    @Override
    public void detach()
    {
        //log.debug(".detach");
        policies = null;
        policy = null;
    }

    private List<PwPolicy> getList( PwPolicy policy )
    {
        List<PwPolicy> policiesList = null;
        
        try
        {
            String szPolicyNm = policy != null ? policy.getName() : "";
            LOG.debug( ".getList policyNm: " + szPolicyNm );
            policiesList = pwPolicyMgr.search( szPolicyNm );
        }
        catch ( SecurityException se )
        {
            String error = ".getList caught SecurityException=" + se;
            LOG.warn( error );
        }
        
        return policiesList;
    }
}