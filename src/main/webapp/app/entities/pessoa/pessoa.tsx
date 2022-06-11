import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { openFile, byteSize, Translate, TextFormat, getSortState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPessoa } from 'app/shared/model/pessoa.model';
import { getEntities } from './pessoa.reducer';

export const Pessoa = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'name'), props.location.search)
  );

  const pessoaList = useAppSelector(state => state.pessoa.entities);
  const loading = useAppSelector(state => state.pessoa.loading);
  const totalItems = useAppSelector(state => state.pessoa.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [props.location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const { match } = props;

  return (
    <div>
      <h2 id="pessoa-heading" data-cy="PessoaHeading">
        <Translate contentKey="provaPesquisadorApp.pessoa.home.title">Pessoas</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="provaPesquisadorApp.pessoa.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/pessoa/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="provaPesquisadorApp.pessoa.home.createLabel">Create new Pessoa</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {pessoaList && pessoaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                {/* <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th> */}
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.name">Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('cpf')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.cpf">Cpf</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('email')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.email">Email</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('avatar')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.avatar">Avatar</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('birthDate')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.birthDate">Birth Date</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                {/* <th className="hand" onClick={sort('excluded')}>
                  <Translate contentKey="provaPesquisadorApp.pessoa.excluded">Excluded</Translate> <FontAwesomeIcon icon="sort" />
                </th> */}
                <th />
              </tr>
            </thead>
            <tbody>
              {pessoaList.map((pessoa, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  {/* <td>
                    <Button tag={Link} to={`/pessoa/${pessoa.id}`} color="link" size="sm">
                      {pessoa.id}
                    </Button>
                  </td> */}
                  <td>{pessoa.name}</td>
                  <td>{pessoa.cpf}</td>
                  <td>{pessoa.email}</td>
                  <td>
                    {pessoa.avatar ? (
                      <div>
                        {pessoa.avatarContentType ? (
                          <a onClick={openFile(pessoa.avatarContentType, pessoa.avatar)}>
                            <img src={`data:${pessoa.avatarContentType};base64,${pessoa.avatar}`} style={{ maxHeight: '30px' }} />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {pessoa.avatarContentType}, {byteSize(pessoa.avatar)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{pessoa.birthDate ? <TextFormat type="date" value={pessoa.birthDate} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  {/* <td>{pessoa.excluded ? 'true' : 'false'}</td> */}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/pessoa/${pessoa.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/pessoa/${pessoa.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/pessoa/${pessoa.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="provaPesquisadorApp.pessoa.home.notFound">No Pessoas found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={pessoaList && pessoaList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Pessoa;
