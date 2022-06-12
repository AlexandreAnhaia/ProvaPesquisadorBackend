import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import React, { useEffect, useState } from 'react';
import { isEmail, Translate, translate, ValidatedBlobField, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { checkExists } from './pessoa-reducer-custom';
import { createEntity, getEntity, reset, updateEntity } from './pessoa.reducer';
import { REGEX_CPF } from 'app/config/constants';

export const PessoaUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();
  const [cpfState, setCpfState] = useState(null);

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const pessoaEntity = useAppSelector(state => state.pessoa.entity);
  const loading = useAppSelector(state => state.pessoa.loading);
  const updating = useAppSelector(state => state.pessoa.updating);
  const updateSuccess = useAppSelector(state => state.pessoa.updateSuccess);
  const cpfExists = useAppSelector(state => state.pessoaReducerCustom.exists);
  const handleClose = () => {
    props.history.push('/pessoa' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (cpfState) {
      dispatch(checkExists(cpfState));
    }
  }, [cpfState]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const setStateValue = value => {
    setTimeout(() => {
      setCpfState(value);
    });
  };

  const saveEntity = values => {
    const entity = {
      ...pessoaEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...pessoaEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="provaPesquisadorApp.pessoa.home.createOrEditLabel" data-cy="PessoaCreateUpdateHeading">
            <Translate contentKey="provaPesquisadorApp.pessoa.home.createOrEditLabel">Create or edit a Pessoa</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {/* {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="pessoa-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null} */}
              <ValidatedField
                label={translate('provaPesquisadorApp.pessoa.name')}
                id="pessoa-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 150, message: translate('entity.validation.maxlength', { max: 150 }) },
                }}
              />
              <ValidatedField
                label={translate('provaPesquisadorApp.pessoa.cpf')}
                id="pessoa-cpf"
                name="cpf"
                data-cy="cpf"
                type="text"
                disabled={!isNew}
                onChange={e => setStateValue(e.target.value)}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: () => !cpfExists || translate('provaPesquisadorApp.pessoa.home.cpfExists'),
                  pattern: { value: REGEX_CPF, message: translate('provaPesquisadorApp.pessoa.home.cpfPattern') },
                }}
              />
              <ValidatedField
                label={translate('provaPesquisadorApp.pessoa.email')}
                id="pessoa-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 400, message: translate('entity.validation.maxlength', { max: 400 }) },
                  validate: v => isEmail(v) || translate('provaPesquisadorApp.pessoa.home.invalid'),
                }}
              />
              <ValidatedBlobField
                label={translate('provaPesquisadorApp.pessoa.avatar')}
                id="pessoa-avatar"
                name="avatar"
                data-cy="avatar"
                isImage
                accept="image/*"
                validate={{}}
              />
              <ValidatedField
                label={translate('provaPesquisadorApp.pessoa.birthDate')}
                id="pessoa-birthDate"
                name="birthDate"
                data-cy="birthDate"
                type="date"
              />
              {/* <ValidatedField
                label={translate('provaPesquisadorApp.pessoa.excluded')}
                id="pessoa-excluded"
                name="excluded"
                data-cy="excluded"
                check
                type="checkbox"
              /> */}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/pessoa" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PessoaUpdate;
