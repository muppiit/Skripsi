/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, DatePicker, Select } from "antd";

const { TextArea } = Input;
const { Option } = Select;

const AddLectureForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  religion,
  user,
  studyProgram,
}) => {
  const [form] = Form.useForm();

  const getUserStudyProgramId = (selectedUser) =>
    selectedUser?.school?.idSchool ||
    selectedUser?.study_program?.idSchool ||
    selectedUser?.studyProgram?.id ||
    selectedUser?.study_program?.id;

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible, form]);

  const handleUserChange = (userId) => {
    const selectedUser = (user || []).find((item) => item.id === userId);
    const studyProgramId = getUserStudyProgramId(selectedUser);
    const selectedStudyProgram = (studyProgram || []).find((item) => item.id === studyProgramId);
    form.setFieldsValue({
      name: selectedUser?.name || form.getFieldValue("name"),
      idStudyProgram: studyProgramId || undefined,
      study_program_name: selectedStudyProgram?.name,
    });
  };

  const handleReligionChange = (religionId) => {
    const selectedReligion = (religion || []).find((item) => item.id === religionId);
    form.setFieldsValue({ religion_name: selectedReligion?.name });
  };

  const handleStudyProgramChange = (studyProgramId) => {
    const selectedStudyProgram = (studyProgram || []).find((item) => item.id === studyProgramId);
    form.setFieldsValue({ study_program_name: selectedStudyProgram?.name });
  };

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  return (
    <Modal
      title="Tambah Guru"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleOk}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form
        {...formItemLayout}
        form={form}
        initialValues={{ status: "dosen" }}
      >
        <Form.Item
          label="User Login:"
          name="user_id"
          rules={[{ required: true, message: "Silahkan pilih user login dosen" }]}
        >
          <Select
            showSearch
            placeholder="Pilih User Login Guru"
            optionFilterProp="children"
            onChange={handleUserChange}
          >
            {(user || []).map((item) => (
              <Option key={item.id} value={item.id}>
                {item.username} - {item.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="NIP:"
          name="nip"
          rules={[{ required: true, message: "NIP wajib diisi" }]}
        >
          <Input placeholder="NIP" />
        </Form.Item>

        <Form.Item
          label="Nama:"
          name="name"
          rules={[{ required: true, message: "Nama wajib diisi" }]}
        >
          <Input placeholder="Nama" />
        </Form.Item>

        <Form.Item
          label="Tempat Lahir:"
          name="place_born"
          rules={[{ required: true, message: "Tempat Lahir wajib diisi" }]}
        >
          <Input placeholder="Tempat Lahir" />
        </Form.Item>

        <Form.Item
          label="Tanggal Lahir:"
          name="date_born"
          rules={[{ required: true, message: "Tanggal Lahir wajib diisi" }]}
        >
          <DatePicker placeholder="Tanggal Lahir" style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item
          label="Gender:"
          name="gender"
          rules={[{ required: true, message: "Gender wajib diisi" }]}
        >
          <Select style={{ width: 120 }} placeholder="Gender">
            <Select.Option value="L">Laki-laki</Select.Option>
            <Select.Option value="P">Perempuan</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="Nomor Telepon:"
          name="phone"
          rules={[{ required: true, message: "Nomor telefon wajib diisi" }]}
        >
          <Input type="number" placeholder="Nomor Telefon (62)" />
        </Form.Item>

        <Form.Item name="status" hidden>
          <Input placeholder="Status" />
        </Form.Item>

        <Form.Item name="religion_name" hidden>
          <Input />
        </Form.Item>

        <Form.Item name="study_program_name" hidden>
          <Input />
        </Form.Item>

        <Form.Item
          label="Alamat:"
          name="address"
          rules={[{ required: true, message: "Alamat wajib diisi" }]}
        >
          <TextArea placeholder="Alamat" />
        </Form.Item>

        <Form.Item
          label="Agama:"
          name="religion_id"
          rules={[{ required: true, message: "Silahkan pilih agama" }]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Agama" onChange={handleReligionChange}>
            {(religion || []).map((arr, key) => (
              <Select.Option value={arr.id} key={`religion-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Program Studi:"
          name="idStudyProgram"
          rules={[{ required: true, message: "Silahkan pilih program studi" }]}
        >
          <Select placeholder="Pilih Program Studi" onChange={handleStudyProgramChange}>
            {(studyProgram || []).map((program) => (
              <Option key={program.id} value={program.id}>
                {program.name}
              </Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddLectureForm;
