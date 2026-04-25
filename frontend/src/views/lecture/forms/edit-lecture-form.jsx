/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, Modal, DatePicker, Select } from "antd";
import moment from "moment";

const { TextArea } = Input;
const { Option } = Select;

const EditLectureForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
  religion,
  studyProgram,
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible && currentRowData) {
      form.setFieldsValue({
        id: currentRowData.id,
        nip: currentRowData.nip,
        name: currentRowData.name,
        place_born: currentRowData.place_born,
        date_born: currentRowData.date_born ? moment(currentRowData.date_born) : null,
        gender: currentRowData.gender,
        phone: currentRowData.phone,
        status: currentRowData.status,
        address: currentRowData.address,
        religion_id: currentRowData.religion?.id,
        idStudyProgram: currentRowData.studyProgram?.id || currentRowData.study_program?.id,
      });
    }
  }, [visible, currentRowData, form]);

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
      title="Edit Guru"
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
      <Form {...formItemLayout} form={form}>
        <Form.Item label="ID Dosen:" name="id">
          <Input disabled />
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
          <Select style={{ width: 300 }} placeholder="Pilih Agama">
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
          <Select placeholder="Pilih Program Studi">
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

export default EditLectureForm;
