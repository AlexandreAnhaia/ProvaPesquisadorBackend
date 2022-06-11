import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Pessoa from './pessoa';
import PessoaDetail from './pessoa-detail';
import PessoaUpdate from './pessoa-update';
import PessoaDeleteDialog from './pessoa-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PessoaUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PessoaUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PessoaDetail} />
      <ErrorBoundaryRoute path={match.url} component={Pessoa} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PessoaDeleteDialog} />
  </>
);

export default Routes;
