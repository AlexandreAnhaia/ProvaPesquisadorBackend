import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './pessoa.reducer';

export const PessoaDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const pessoaEntity = useAppSelector(state => state.pessoa.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pessoaDetailsHeading">
          <Translate contentKey="provaPesquisadorApp.pessoa.detail.title">Pessoa</Translate>
        </h2>
        <dl className="jh-entity-details">
          {/* <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{pessoaEntity.id}</dd> */}
          <dt>
            <span id="name">
              <Translate contentKey="provaPesquisadorApp.pessoa.name">Name</Translate>
            </span>
          </dt>
          <dd>{pessoaEntity.name}</dd>
          <dt>
            <span id="cpf">
              <Translate contentKey="provaPesquisadorApp.pessoa.cpf">Cpf</Translate>
            </span>
          </dt>
          <dd>{pessoaEntity.cpf}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="provaPesquisadorApp.pessoa.email">Email</Translate>
            </span>
          </dt>
          <dd>{pessoaEntity.email}</dd>
          <dt>
            <span id="avatar">
              <Translate contentKey="provaPesquisadorApp.pessoa.avatar">Avatar</Translate>
            </span>
          </dt>
          <dd>
            {pessoaEntity.avatar ? (
              <div>
                {pessoaEntity.avatarContentType ? (
                  <a onClick={openFile(pessoaEntity.avatarContentType, pessoaEntity.avatar)}>
                    <img src={`data:${pessoaEntity.avatarContentType};base64,${pessoaEntity.avatar}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {pessoaEntity.avatarContentType}, {byteSize(pessoaEntity.avatar)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="birthDate">
              <Translate contentKey="provaPesquisadorApp.pessoa.birthDate">Birth Date</Translate>
            </span>
          </dt>
          <dd>
            {pessoaEntity.birthDate ? <TextFormat value={pessoaEntity.birthDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          {/* <dt>
            <span id="excluded">
              <Translate contentKey="provaPesquisadorApp.pessoa.excluded">Excluded</Translate>
            </span>
          </dt>
          <dd>{pessoaEntity.excluded ? 'true' : 'false'}</dd> */}
        </dl>
        <Button tag={Link} to="/pessoa" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/pessoa/${pessoaEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PessoaDetail;
